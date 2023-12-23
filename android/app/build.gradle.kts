plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.svbackend.natai"
        minSdk = 29
        targetSdk = 34
        versionCode = 15
        versionName = "1.5.9"
        buildConfigField("String", "API_BASE_URL", "\"https://legally-ideal-macaw.ngrok-free.app\"")
        buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"pk_test_51N47wRCN1hwEuS9VkiczMeL0YVViEvifpBeiULw6ZXzEdPUPAX7DmPzNcLCqsTfp9xEdrChnICgdER61zvnhgqMc00oEooAKPk\"")
        //buildConfigField("String", "API_BASE_URL", "\"https://natai.app\"")

        kapt {
            arguments { arg("room.schemaLocation", "$projectDir/schemas") }
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            buildConfigField("String", "API_BASE_URL", "\"https://natai.app\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        // suppressKotlinVersionCompatibilityCheck
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    namespace = "com.svbackend.natai.android"

    bundle {
        storeArchive {
            enable = false
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("androidx.core:core-ktx:1.12.0")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    val fragmentVersion = "1.6.2"
    implementation("androidx.fragment:fragment:$fragmentVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    val ktorVersion = "2.3.7"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")

    val composeVersion = "1.5.4"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.animation:animation-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    implementation("com.google.android.material:compose-theme-adapter-3:1.1.1")

    implementation("io.coil-kt:coil-compose:2.2.2")

    // stripe
    implementation("com.stripe:stripe-android:20.28.1")

    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")
}