plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
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
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation ("com.opencsv:opencsv:5.5")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.google.maps:google-maps-services:2.2.0")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    implementation("androidx.test:core-ktx:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
<<<<<<< HEAD
=======

    androidTestImplementation("org.mockito:mockito-android:3.+")

>>>>>>> main
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.camera:camera-core:1.3.0-alpha06")
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha06")
    implementation ("androidx.camera:camera-video:1.3.0-alpha06")
    implementation ("androidx.camera:camera-view:1.3.0-alpha06")
    implementation ("androidx.camera:camera-mlkit-vision:1.3.0-alpha06")
    implementation ("androidx.camera:camera-extensions:1.3.0-alpha06")
//    implementation ("com.android.support:multidex:2.0.1")

    implementation ("androidx.multidex:multidex:2.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.5.2")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("org.json:json:20210307")
    implementation("com.amazonaws:aws-android-sdk-sqs:2.73.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
//    implementation() fileTree(dir: 'libs', include: ['*.jar'])
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.amazonaws:aws-android-sdk-ddb:2.73.0")
    implementation("com.amazonaws:aws-android-sdk-mobile-client:2.73.0")
    implementation("com.amazonaws:aws-android-sdk-core:2.73.0")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")
<<<<<<< HEAD
=======

>>>>>>> main
//    implementation ("com.amazonaws:aws-android-sdk-dynamodb:2.x.x")
    // https://mvnrepository.com/artifact/com.amazonaws/aws-android-sdk-ddb-mapper
    implementation ("com.amazonaws:aws-android-sdk-ddb-mapper:2.73.0")

}

afterEvaluate {
    tasks.findByName("mergeDebugResources")?.dependsOn("processDebugGoogleServices")
}
