package org.modelix.model.server.store

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.modelix.model.IGenericKeyListener
import kotlin.time.Duration.Companion.seconds

interface IStoreClient : IGenericStoreClient<String>

suspend fun pollEntry(storeClient: IsolatingStore, key: ObjectInRepository, lastKnownValue: String?): String? {
    var result: String? = null
    coroutineScope {
        var handlerCalled = false
        val callHandler: suspend (String?) -> Unit = {
            handlerCalled = true
            result = it
        }

        val channel = Channel<Unit>(Channel.RENDEZVOUS)

        val listener = object : IGenericKeyListener<ObjectInRepository> {
            override fun changed(key: ObjectInRepository, value: String?) {
                launch {
                    callHandler(value)
                    channel.trySend(Unit)
                }
            }
        }
        try {
            storeClient.listen(key, listener)
            if (lastKnownValue != null) {
                // This could be done before registering the listener, but
                // then we have to check it twice,
                // because the value could change between the first read and
                // registering the listener.
                // Most of the time the value will be equal to the last
                // known value.
                // Registering the listener without needing it is less
                // likely to happen.
                @OptIn(RequiresTransaction::class)
                val value = storeClient.runReadTransaction { storeClient[key] }
                if (value != lastKnownValue) {
                    callHandler(value)
                    return@coroutineScope
                }
            }
            withTimeoutOrNull(25.seconds) {
                channel.receive() // wait until the listener is called
            }
        } finally {
            storeClient.removeListener(key, listener)
        }
        if (!handlerCalled) {
            @OptIn(RequiresTransaction::class)
            result = storeClient.runReadTransaction { storeClient[key] }
        }
    }
    return result
}
