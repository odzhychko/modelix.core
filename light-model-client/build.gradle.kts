plugins {
    `modelix-kotlin-multiplatform`
    `maven-publish`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":model-api"))
                implementation(project(":model-api-gen-runtime"))
                implementation(project(":model-server-api"))
                implementation(project(":modelql-core"))
                implementation(project(":modelql-client"))

                api(project(":modelql-untyped"))

                implementation(libs.modelix.incremental)
                implementation(libs.ktor.client.websockets)
                implementation(libs.kotlin.stdlib.common)
                implementation(libs.kotlin.logging)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.kotlin.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
        val jvmTest by getting {
            dependencies {

                implementation(project(":authorization"))
                implementation(project(":model-client", configuration = "jvmRuntimeElements"))
                implementation(project(":model-server"))
                implementation(project(":model-server-lib"))
                implementation(libs.modelix.incremental)

                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.cors)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.html.builder)
                implementation(libs.ktor.server.auth)
                implementation(libs.ktor.server.auth.jwt)
                implementation(libs.ktor.server.status.pages)
                implementation(libs.ktor.server.forwarded.header)
                implementation(libs.ktor.server.websockets)
                implementation(libs.ktor.server.test.host)
                implementation(libs.ktor.server.resources)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(npm("jsdom-global", "3.0.2"))
                implementation(npm("jsdom", "20.0.2"))
            }
        }
    }
}
