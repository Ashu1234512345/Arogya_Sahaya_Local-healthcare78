plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)


}

android {
    namespace = "com.example.arogyasahayalocal"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.arogyasahayalocal"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)
    val room_version = "2.6.1"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
  //  implementation(libs.androidx.room3.compiler)
    implementation(libs.androidx.ui)
    implementation("com.google.code.gson:gson:2.10.1")
  //  implementation(libs.androidx.room.common.jvm)
    implementation(libs.androidx.room.ktx)
    implementation("com.google.code.gson:gson:2.10.1")
   // implementation(libs.androidx.room.runtime.jvm)
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0") // Check for the latest version
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}
configurations.all {
    exclude(group = "com.intellij", module = "annotations")

    resolutionStrategy {
        force("androidx.room:room-runtime:2.8.4")
    }
}





//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//}
//
//android {
//    namespace = "com.example.arogya_sahaya_local"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.arogya_sahaya_local"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    buildFeatures {
//        compose = true
//    }
//}
//
//dependencies {
//
//    // Compose BOM
//    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
//
//    // Core
//    implementation("androidx.core:core-ktx:1.12.0")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
//
//    // Compose
//    implementation("androidx.activity:activity-compose:1.8.2")
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
//
//    debugImplementation("androidx.compose.ui:ui-tooling")
//}