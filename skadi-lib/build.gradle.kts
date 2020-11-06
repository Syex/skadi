plugins {
    kotlin("jvm")
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

repositories {
    // for flow test observer
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.coroutines}")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:${Dependencies.jUnit}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.coroutines}")
    testImplementation("com.google.truth:truth:${Dependencies.truth}")
    testImplementation("app.cash.turbine:turbine:${Dependencies.turbine}")
}

val siteUrl = "https://github.com/Syex/skadi"
val vcsUrl = "https://github.com/Syex/skadi.git"
val libDescription = "A Kotlin JVM library featuring a redux-like architecture with coroutines"

val libVersion = "0.2.0"

// maven publish plugin
java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("skadi") {
            group = "de.syex"
            artifactId = "skadi"
            version = libVersion
            from(components["java"])

            pom {
                name.set("skadi")
                description.set(libDescription)

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("Syex")
                        name.set("Tom Seifert")
                    }
                }

                scm {
                    connection.set(vcsUrl)
                    developerConnection.set(vcsUrl)
                    url.set(siteUrl)
                }
            }
        }
    }
}

// bintray plugin
bintray {
    user = System.getenv("bintrayUser")
    key = System.getenv("bintrayApiKey")
    setPublications("skadi")
    override = true
    pkg.apply {
        repo = "skadi"
        name = "skadi"
        setLicenses("Apache-2.0")
        vcsUrl = vcsUrl
        publish = true
        desc = libDescription
        websiteUrl = siteUrl

        version.apply {
            name = libVersion
        }
    }
}
