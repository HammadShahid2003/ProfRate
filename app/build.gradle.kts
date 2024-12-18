plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.profrate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.profrate"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.firebase.auth.v2211)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")
    implementation ("com.mikhaellopez:circularimageview:4.3.1")
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.cloudinary:cloudinary-android:3.0.2")

}