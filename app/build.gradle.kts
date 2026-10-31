plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.tobirama.sensi"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.tobirama.sensi"
        minSdk = 23
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}
