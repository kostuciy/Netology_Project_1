plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")

    }

android {
    namespace = "ru.netology.nmedia"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.netology.nmedia"
        minSdk = 23
        targetSdk = 33
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
            manifestPlaceholders["usesCleartextTraffic"] = false
            buildConfigField("String", "BASE_URL", "\"https://nmedia.ru\"")
        }
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = true
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:9999\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val core_version = "1.9.0"
    val appcompat_version = "1.5.1"
    val constraintlayout_version = "2.1.4"
    val swiperefreshlayout_version = "1.1.0"
    val coordinatorlayout_version = "1.2.0"
    val recyclerview_version = "1.2.1"
    val junit_version = "4.13.2"
    val ext_junit_version = "1.1.3"
    val espresso_core_version = "3.4.0"
    val activity_version = "1.6.0"
    val lifecycle_version = "2.5.1"
    val mdc_version = "1.6.1"
    val nav_version = "2.5.2"
    val room_version = "2.6.0-alpha02"
    val firebase_version = "30.3.1"
    val glide_version = "4.16.0"
    val retrofit_version = "2.9.0"
    val retrofitgson_version = "2.9.0"
    val okhttplogging_version = "4.12.0"
    val coroutines_version = "1.7.3"
    val play_services_base_version = "18.1.0"

//    implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation("androidx.core:core-ktx:$core_version")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.constraintlayout:constraintlayout:$constraintlayout_version")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:$swiperefreshlayout_version")
    implementation("androidx.coordinatorlayout:coordinatorlayout:$coordinatorlayout_version")
    implementation("androidx.recyclerview:recyclerview:$recyclerview_version")
    implementation ("androidx.activity:activity-ktx:$activity_version")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    kapt ("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")
    implementation ("com.google.android.material:material:$mdc_version")
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    implementation(platform("com.google.firebase:firebase-bom:$firebase_version"))
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofitgson_version")
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttplogging_version")
    implementation ("com.github.bumptech.glide:glide:$glide_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    implementation ("com.google.android.gms:play-services-base:$play_services_base_version")

    testImplementation ("junit:junit:$junit_version")
    androidTestImplementation ("androidx.test.ext:junit:$ext_junit_version")
    androidTestImplementation ("androidx.test.espresso:espresso-core:$espresso_core_version")
}