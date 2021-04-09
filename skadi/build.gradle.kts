plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
//    signing
}

kotlin {
    jvm()
    android {
        publishLibraryVariants("release", "debug")
    }
    ios()
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

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(1)
        targetSdkVersion(30)
    }
}

publishing {
    repositories {
        val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases"
        val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
        val repoUrl = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

        maven(url = repoUrl) {
            authentication {
                credentials {
                    username = System.getenv("OSS_SONATYPE_USERNAME")
                    password = System.getenv("OSS_SONATYPE_PASSWORD")
                }
            }
        }
    }
}

//@Suppress("UnstableApiUsage")
//signing {
//    val signingPrivateKey = System.getenv("MAVEN_GPG_PRIVATE_KEY")
//    val signingPassword = System.getenv("MAVEN_GPG_PASSPHRASE")
//    useInMemoryPgpKeys(signingPrivateKey, signingPassword)
//    sign(publishing.publications)
//}
