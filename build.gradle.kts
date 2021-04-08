plugins {
    kotlin("multiplatform") version "1.4.32"
}

repositories {
    google()
    jcenter()
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

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
    }
}

project(":androidSample") {
    repositories {
        google()
        jcenter()
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType(Test::class.java) {
        useJUnitPlatform()
        testLogging { events("passed", "skipped", "failed") }
    }
}
