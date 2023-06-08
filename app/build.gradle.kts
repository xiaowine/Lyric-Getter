import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 33
    val buildTime = System.currentTimeMillis()
    defaultConfig {
        applicationId = "cn.lyrics.getter"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0$buildTime"
        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false
        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
    }

    buildTypes {
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
    }
    namespace = "cn.lyrics.getter"
    applicationVariants.all {
        outputs.all {
            (this as BaseVariantOutputImpl).outputFileName =
                "Lyrics Getter-$versionName($versionCode)-$name-$buildTime.apk"
        }
    }
}


dependencies {
    implementation("com.github.kyuubiran:EzXHelper:2.0.5")
    implementation("org.luckypray:DexKit:1.1.8")
    implementation(project(mapOf("path" to ":Getter:Api")))
    compileOnly("de.robv.android.xposed:api:82")
}
