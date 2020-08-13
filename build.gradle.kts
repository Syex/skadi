plugins {
    kotlin("jvm") version "1.3.72"
}

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    // This is disabled because IntelliJ becomes sluggish as hell when new type inference is enabled. Left here to
    // check for a fix with newer version
    // Enable new Kotlin type inference algorithm
//    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
//        kotlinOptions {
//            freeCompilerArgs = freeCompilerArgs + "-XXLanguage:+NewInference"
//        }
//    }

    // Enable JUnit5
    tasks.withType(Test::class.java) {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
