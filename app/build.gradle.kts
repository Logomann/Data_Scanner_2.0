import java.io.FileInputStream
import java.util.Properties

val developPropertiesFile = rootProject.file("develop.properties")
val developProperties = Properties()
developProperties.load(FileInputStream(developPropertiesFile))

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.logomann.datascanner20"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.logomann.datascanner20"
        minSdk = 29
        targetSdk = 35
        versionCode = 7
        versionName = "1.1.1"
        buildConfigField(type = "String", name = "SERVER_IP", value = "\"${developProperties["serverIp"]}\"")
        buildConfigField(type = "String", name = "DB_LOGIN", value = "\"${developProperties["dbLogin"]}\"")
        buildConfigField(type = "String", name = "DB_PASSWORD", value = "\"${developProperties["dbPassword"]}\"")
        buildConfigField(type = "String", name = "DB_NAME", value = "\"${developProperties["dbName"]}\"")
        buildConfigField(type = "String", name = "DB_PORT", value = "\"${developProperties["dbPort"]}\"")
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
    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = "35.0.0 rc3"
}
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.koin.androidx.compose)
    implementation (libs.ui)
    implementation(libs.androidx.constraintlayout.compose.v110alpha14)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.koin.android)
    implementation(libs.postgresql)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.video)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
    implementation (libs.barcode.scanning)
    implementation (libs.androidx.camera.mlkit.vision)
}
