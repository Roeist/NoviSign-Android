//@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
   id("com.google.dagger.hilt.android")

}

android {
    namespace = "com.steinberg.novisign"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.steinberg.novisign"
//        minSdk = 24
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Test
    // Required -- JUnit 4 framework
    val jUnitVersion = "4.12"
    testImplementation ("junit:junit:$jUnitVersion")
    // Optional -- Robolectric environment
    val androidXTestVersion = "1.6.1"
    testImplementation("androidx.test:core:$androidXTestVersion")
    // Optional -- Mockito framework
    val mockitoVersion = "5.14.2"
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    // Optional -- mockito-kotlin
    val mockitoKotlinVersion = "5.4.0"
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    // Optional -- Mockk framework
    val mockkVersion = "1.13.13"
    testImplementation("io.mockk:mockk:$mockkVersion")

    //hilt
    implementation(libs.hilt.android)
    val hilt =  "2.51.1"

//    annotationProcessor ("com.google.dagger:hilt-compiler:2.52")
//    annotationProcessor ("com.google.dagger:hilt-compiler:$hilt")
    ksp(libs.hilt.android.compiler)

    androidTestImplementation ("com.google.dagger:hilt-android-testing:$hilt")
    kspAndroidTest("com.google.dagger:hilt-compiler:$hilt")

    //videoModel mvvm
 /*   implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)
    implementation ("android.arch.lifecycle:extensions:1.1.1")

    implementation(libs.androidx.lifecycle.viewmodel.ktx)*/
    val lifecycle_version = "2.8.7"

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("android.arch.lifecycle:extensions:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    val media3_version = "1.4.1"

    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3_version")
    implementation("androidx.media3:media3-ui:$media3_version")
    implementation("androidx.media3:media3-exoplayer-dash:$media3_version")

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.9")

    //Room Database
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")


}



