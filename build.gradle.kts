plugins {
    kotlin("jvm") version "1.3.71"
}

group "de.syex"
version "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.5")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

// Enable JUnit5
tasks.withType(Test::class.java) {
    @Suppress("UnstableApiUsage")
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Enable new Kotlin type inference algorithm
tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-XXLanguage:+NewInference"
    }
}
