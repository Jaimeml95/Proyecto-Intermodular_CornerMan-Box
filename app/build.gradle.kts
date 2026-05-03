plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jaimemoro.cornermanbox"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.jaimemoro.cornermanbox"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Variables para Spotify Auth
        manifestPlaceholders["redirectSchemeName"] = "cornermanbox"
        manifestPlaceholders["redirectHostName"] = "callback"
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
    val nav_version = "2.7.7" //Comprobar luego compatibilidad con el navegador

    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    //Implementación para Spotify
    // Spotify App Remote y Auth
    implementation("com.spotify.android:auth:2.1.0")
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    // Gson para manejar los metadatos de las canciones si fuera necesario
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}