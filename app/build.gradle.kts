import java.util.UUID

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jaimemoro.cornermanbox"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jaimemoro.cornermanbox"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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

    // Decirle a Android que use la carpeta generada para los Assets (Vosk)
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "${layout.buildDirectory.get()}/generated/assets")
        }
    }
}

dependencies {
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("com.spotify.android:auth:2.1.0")
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation("com.google.code.gson:gson:2.10.1")

    // Vosk
    implementation("com.alphacephei:vosk-android:0.3.47")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Tarea automática para generar el archivo UUID en la carpeta model-es
tasks.register("genUUID") {
    val outputDir = file("${layout.buildDirectory.get()}/generated/assets/model-es")
    val uuidFile = file("$outputDir/uuid")

    doLast {
        if (!outputDir.exists()) outputDir.mkdirs()
        uuidFile.writeText(UUID.randomUUID().toString())

    }
}

// Ejecutar la tarea siempre antes de compilar
tasks.named("preBuild") {
    dependsOn("genUUID")
}