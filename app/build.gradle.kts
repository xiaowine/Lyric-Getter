import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 34
    val buildTime = System.currentTimeMillis()
    val apiVersion = 4
    defaultConfig {
        applicationId = "cn.lyric.getter"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.0.3.$apiVersion"

        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")
        buildConfigField("int", "API_VERSION", "$apiVersion")
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
        viewBinding = true
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
    implementation(project(mapOf("path" to ":LyricGetterApi")))
//    为啥ci会找不到呢？被迫手动导入jar
    compileOnly("de.robv.android.xposed:api:82")
//    compileOnly(files("libs/api-82.jar"))
}
