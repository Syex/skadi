plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    iosArm32()
    iosArm64()
    iosX64()
    linuxX64()
    macosX64()
    mingwX64()
    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.coroutines}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("app.cash.turbine:turbine:${Dependencies.turbine}")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.jupiter:junit-jupiter:${Dependencies.jUnit}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.coroutines}")
            }

            tasks.named("jvmTest", Test::class.java) {
                useJUnitPlatform()
                testLogging { events("passed", "skipped", "failed") }
            }
        }
    }
}

//plugins {
//    kotlin("jvm")
//    `maven-publish`
//    signing
//}
//
//dependencies {
//    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.coroutines}")
//}
//
//dependencies {
//    testImplementation("org.junit.jupiter:junit-jupiter:${Dependencies.jUnit}")
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.coroutines}")
//    testImplementation("com.google.truth:truth:${Dependencies.truth}")
//    testImplementation("app.cash.turbine:turbine:${Dependencies.turbine}")
//}
//
//val siteUrl = "https://github.com/Syex/skadi"
//val vcsUrl = "https://github.com/Syex/skadi.git"
//val libDescription = "A Kotlin JVM library featuring a redux-like architecture with coroutines"
//
//val libVersion = "0.4.0"
//
//@Suppress("UnstableApiUsage")
//java {
//    withJavadocJar()
//    withSourcesJar()
//}
//
//publishing {
//    repositories {
//        val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases"
//        val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//        val repoUrl = uri(if (libVersion.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
//
//        maven(url = repoUrl) {
//            authentication {
//                credentials {
//                    username = System.getenv("OSS_SONATYPE_USERNAME")
//                    password = System.getenv("OSS_SONATYPE_PASSWORD")
//                }
//            }
//        }
//    }
//
//    publications {
//        create<MavenPublication>("skadi") {
//            group = "io.github.syex"
//            artifactId = "skadi"
//            version = libVersion
//            from(components["java"])
//
//            pom {
//                name.set("skadi")
//                description.set(libDescription)
//
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//
//                developers {
//                    developer {
//                        id.set("Syex")
//                        name.set("Tom Seifert")
//                    }
//                }
//
//                scm {
//                    connection.set(vcsUrl)
//                    developerConnection.set(vcsUrl)
//                    url.set(siteUrl)
//                }
//            }
//        }
//    }
//}
//
//@Suppress("UnstableApiUsage")
//signing {
//    val signingPrivateKey = System.getenv("MAVEN_GPG_PRIVATE_KEY")
//    val signingPassword = System.getenv("MAVEN_GPG_PASSPHRASE")
//    useInMemoryPgpKeys(signingPrivateKey, signingPassword)
//    sign(publishing.publications["skadi"])
//}
