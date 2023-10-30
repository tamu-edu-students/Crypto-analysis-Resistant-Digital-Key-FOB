plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.digitalkeyfobcomp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.digitalkeyfobcomp"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}




dependencies {

    implementation ("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation ("com.google.dagger:hilt-android:2.48.1")
    kapt ("com.google.dagger:hilt-android-compiler:2.48.1")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")



    implementation("com.google.firebase:firebase-crashlytics:18.5.0")
    implementation("com.google.firebase:firebase-analytics:21.4.0")
    //    def (room_version = "2.5.0")
//    implementation ("androidx.room:room-ktx:$room_version")
//    kapt ("androidx.room:room-compiler:$room_version")

    kapt ("androidx.room:room-compiler:2.6.0")
//    implementation ("androidx.room:room-ktx:2.6.0")
//    ksp("androidx.room:room-compiler:2.6.0")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation("androidx.room:room-testing:2.6.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:core:1.5.0")
    //room
    implementation ("androidx.room:room-runtime:2.6.0") // Replace with the latest version
    annotationProcessor ("androidx.room:room-compiler:2.6.0") // Replace with the latest version
    implementation ("androidx.room:room-ktx:2.6.0")

    implementation ("se.warting.signature:signature-pad:0.1.2") // jetpack Compose views
    implementation ("androidx.navigation:navigation-compose:2.7.4")
    implementation("androidx.compose.material3:material3:1.2.0-alpha10")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.0-alpha10")
    implementation ("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


}