plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

android {
    namespace = "com.dicoding.moviecatalog.core"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dicoding.moviecatalog.core"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3Y2FhZmE3YTQxMGY3ZWFhNzk0MTQyNTAzMTUwMGNjNSIsIm5iZiI6MTczNDMzMjEzMi43NTQsInN1YiI6IjY3NjA1YzA0NzI2NDE0YWYxYzhmMDY5ZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bJ3Z_wQqDj9-Fj2BhfM3Tq6MdOLPuPjFkUvWXKzxSZM\"")
        buildConfigField("String", "BASE_URL", "\"https://api.themoviedb.org/3/\"")
        buildConfigField("String", "IMAGE_URL", "\"https://image.tmdb.org/t/p/w500\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // SQLCipher & SQLite
    implementation(libs.sqlcipher)
    implementation(libs.androidx.sqlite)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}