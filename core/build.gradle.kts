plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
}

android {
    namespace = "com.dicoding.moviecatalog.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1OTYxYTc2MTMxZjdmMjgwN2FkNjlkODUxNDVlMTI4OSIsIm5iZiI6MTcwOTAwNzMyOC44MzcsInN1YiI6IjY1ZGQ2MWUwYzkyYzVkMDE2MzQ3MTdjNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Y_ZUQ4dy7xrBIp4h0E-TU4RN-IMkKMPRmSls-BnJS3c\"")
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