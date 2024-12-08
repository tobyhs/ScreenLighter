plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.tobyhs.screenlighter"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.tobyhs.screenlighter"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            versionNameSuffix = "-debug"
        }

        create("local") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-local"
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation("androidx.test.ext:junit:1.2.1")
    val espressoVersion = "3.6.1"
    testImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    testImplementation("org.robolectric:robolectric:4.14.1")
}
