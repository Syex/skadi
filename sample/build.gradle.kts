plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "de.syex.skadi.sample"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0.0-SNAPSHOT"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    // jcenter dep for testing for end users, local dep for testing changes
    implementation(project(":skadi-lib"))
//    implementation("de.syex:skadi:0.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.coroutines}")

    implementation("com.google.android.material:material:1.2.0")

    val lifecycleVersion = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    implementation("androidx.constraintlayout:constraintlayout:2.0.0")

    implementation("io.coil-kt:coil:0.9.5")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:${Dependencies.jUnit}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Dependencies.coroutines}")
    testImplementation("com.google.truth:truth:${Dependencies.truth}")
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("app.cash.turbine:turbine:${Dependencies.turbine}")
}
