plugins {
    id("com.android.application") version "9.3.2"
}

android {
    namespace = "com.mad.techfix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mad.techfix"
        minSdk = 24
        targetSdk = 34
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
    // Core Android (Pure Java)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 1. CameraX (Photo Capture) - UPDATED TO 1.4.0 FOR 16 KB PAGE ALIGNMENT
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")

    // 2. Room Database (Pure Java - using annotationProcessor, NOT kapt)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.activity.ktx)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // 3. Retrofit (Internet)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 4. ViewModel & LiveData (Java version)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")

    // 5. Glide (for images)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.cardview:cardview:1.0.0")

    // SwipeRefreshLayout (for pull-to-refresh)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}