plugins {
    kotlin("jvm")
}

group "de.syex"
version "0.1.0-SNAPSHOT"

repositories {
    // for flow test observer
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.coroutines}")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:${Dependencies.jUnit}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.coroutines}")
    testImplementation("com.google.truth:truth:${Dependencies.truth}")
    testImplementation("com.github.ologe:flow-test-observer:1.4.0")
}
