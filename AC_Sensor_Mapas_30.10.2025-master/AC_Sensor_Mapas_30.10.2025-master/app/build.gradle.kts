plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ac_sensor_mapas_30102025"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ac_sensor_mapas_30102025"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation(libs.junit)
    implementation("com.google.android.material:material:1.12.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}