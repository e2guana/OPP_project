plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    // alias(libs.plugins.navigation.safeargs) // SafeArgs 플러그인 적용
}

android {
    namespace = "com.example.opp_e2guana"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.opp_e2guana"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.activity.ktx)      //lms-안드로이드 실습최신버전 반영 게시판-Viewmodel 실습-우상천님의 방법대로 불러옴
    implementation(libs.androidx.fragment.ktx)

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")  //구글지도 API
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)                                  //파이어베이스

    //참조링크 : https://firebase.google.com/docs/auth/android/password-auth?hl=ko&authuser
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth")                //파이어베이스 auth 진행 중 추가한 내용
    implementation("com.google.firebase:firebase-storage") // Firebase Storage 추가



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}