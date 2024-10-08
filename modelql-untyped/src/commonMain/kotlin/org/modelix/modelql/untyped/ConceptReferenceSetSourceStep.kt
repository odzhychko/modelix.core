/*
 * Copyright (c) 2024.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.modelix.modelql.untyped

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.modelix.model.api.ConceptReference
import org.modelix.modelql.core.ConstantSourceStep
import org.modelix.modelql.core.IStep
import org.modelix.modelql.core.QueryDeserializationContext
import org.modelix.modelql.core.QueryGraphDescriptorBuilder
import org.modelix.modelql.core.StepDescriptor
import kotlin.reflect.typeOf

class ConceptReferenceSetSourceStep(val referenceSet: Set<ConceptReference>) : ConstantSourceStep<Set<ConceptReference>>(referenceSet, typeOf<Set<ConceptReference>>()) {
    override fun createDescriptor(context: QueryGraphDescriptorBuilder): StepDescriptor = Descriptor(referenceSet)

    @Serializable
    @SerialName("conceptReferenceSetMonoSource")
    class Descriptor(val referenceSet: Set<ConceptReference>) : StepDescriptor() {
        override fun createStep(context: QueryDeserializationContext): IStep {
            return ConceptReferenceSetSourceStep(referenceSet)
        }
    }

    override fun canEvaluateStatically(): Boolean = true
    override fun evaluateStatically(): Set<ConceptReference> = referenceSet
}

fun Set<ConceptReference>.asMono() = ConceptReferenceSetSourceStep(this)
