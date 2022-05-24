val composeVersion = "1.2.0-beta02"

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.svbackend.natai"
        minSdk = 29
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["auth0Domain"] = "@string/com_auth0_domain"
        manifestPlaceholders["auth0Scheme"] = "@string/com_auth0_scheme"
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8080\"")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"https://natai.app\"")
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
    namespace = "com.svbackend.natai.android"
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.auth0.android:auth0:2.6.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")

    val roomVersion = "2.4.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    val fragmentVersion = "1.4.1"
    implementation("androidx.fragment:fragment:$fragmentVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    val ktorVersion = "2.0.1"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.material3:material3:1.0.0-alpha12")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    implementation("com.google.android.material:compose-theme-adapter-3:1.0.9")

    val navVersion = "2.4.2"
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    val workVersion = "2.7.1"

    // implementation("androidx.work:work-runtime:$workVersion")
    implementation("androidx.work:work-runtime-ktx:$workVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")
}