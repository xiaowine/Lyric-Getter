import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
}

val localProperties = Properties()
if (rootProject.file("local.properties").canRead())
    localProperties.load(rootProject.file("local.properties").inputStream())

android {
    compileSdk = 34
    val buildTime = System.currentTimeMillis()
    val apiVersion = 4
    defaultConfig {
        applicationId = "cn.lyric.getter"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.4.$apiVersion"

        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        buildConfigField("int", "API_VERSION", "$apiVersion")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.majorVersion
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
        buildConfig = true
    }
    namespace = "cn.lyric.getter"
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "Lyrics Getter-$versionName-($versionCode)-$name-$buildTime.apk"
        }
    }
}


dependencies {
    implementation("com.github.kyuubiran:EzXHelper:2.0.6")
    implementation("org.luckypray:DexKit:1.1.8")
    implementation(rootProject.project(":LyricGetterApi"))
    compileOnly("de.robv.android.xposed:api:82")
}
