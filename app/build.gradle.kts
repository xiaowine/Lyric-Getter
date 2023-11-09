import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

val localProperties = Properties()
if (rootProject.file("local.properties").canRead())
    localProperties.load(rootProject.file("local.properties").inputStream())

android {
    compileSdk = 34
    val buildTime = System.currentTimeMillis()
    defaultConfig {
        applicationId = "cn.lyric.getter"
        minSdk = 26
        targetSdk = 34
        versionCode = 15
        versionName = "2.0.7"
        buildConfigField("long", "BUILD_TIME", "$buildTime")
        buildConfigField("int", "API_VERSION", "6")
        buildConfigField("int", "CONFIG_VERSION", "1")
        buildConfigField("int", "APP_RULES_API_VERSION", "2")
    }
    val config = localProperties.getProperty("androidStoreFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = localProperties.getProperty("androidStorePassword")
            keyAlias = localProperties.getProperty("androidKeyAlias")
            keyPassword = localProperties.getProperty("androidKeyPassword")
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                    "proguard-log.pro"
                )
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/**"
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
        }
        dex {
            useLegacyPackaging = true
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    namespace = "cn.lyric.getter"
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "Lyrics Getter-$versionName-$versionCode-$name-$buildTime.apk"
        }
    }
}


dependencies {
    implementation(libs.ezXHelper)
    implementation(libs.dexkit)
    compileOnly(libs.xposed)

    implementation(libs.core.ktx)
    implementation(libs.material)
//    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.gson)
    implementation(libs.lyricGetter.api)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.xkt)
    implementation(libs.dsp)
    implementation(libs.cardSlider)
    implementation(libs.modernandroidpreferences)
    implementation(libs.swiperefreshlayout)

}
