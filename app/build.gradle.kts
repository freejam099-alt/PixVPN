plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.pixvpn.gram"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pixvpn.gram"
        minSdk = 26
        targetSdk = 35
        versionCode = 30
        versionName = "2.9.1"
    }

    // Local verification may remain unsigned. CI must provide all four values and
    // explicitly opt into the release signing config (the workflow enforces this).
    val ksPath = System.getenv("KEYSTORE_FILE")
    val ksPassword = System.getenv("KEYSTORE_PASSWORD")
    val keyAlias = System.getenv("KEY_ALIAS")
    val keyPassword = System.getenv("KEY_PASSWORD")
    signingConfigs {
        if (!ksPath.isNullOrBlank() && file(ksPath).isFile &&
            !ksPassword.isNullOrBlank() && !keyAlias.isNullOrBlank() && !keyPassword.isNullOrBlank()) {
            create("release") {
                storeFile = file(ksPath)
                storePassword = ksPassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // No debug fallback: absent credentials intentionally produces an unsigned
            // local verification APK; CI rejects that configuration before Gradle runs.
            signingConfig = signingConfigs.findByName("release")
        }
    }

    // Distribution target is strictly ARM64-v8a. No 32-bit, x86, or universal APK.
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
            isUniversalApk = false
        }
    }

    // The Aether / Psiphon / Lyrebird executables ship as lib*.so and are run from
    // nativeLibraryDir, so they must be extracted to disk.
    packaging { jniLibs { useLegacyPackaging = true } }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // Kotlin 2.0.21 + Compose compiler plugin; alpha18 is the public Expressive API
    // line used by the UI (MaterialExpressiveTheme, MotionScheme, ButtonGroup, etc.).
    implementation(platform("androidx.compose:compose-bom:2025.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3:1.4.0-alpha18")
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0-alpha18")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    testImplementation("junit:junit:4.13.2")
    // sing-box (libbox), built by the "libbox" job of the GitHub workflow into app/libs/libbox.aar.
    implementation(files("libs/libbox.aar"))
}
