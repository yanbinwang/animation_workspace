plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.animation"
    compileSdk = libs.versions.compileSdkVersion.get().toInt()

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdkVersion.get().toInt()
        targetSdk = libs.versions.targetSdkVersion.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // 减少语言支持
        resourceConfigurations.add("zh")
        // dex 突破 65535 的限制
        multiDexEnabled = true
//        // 告知 Gradle 只打包 hdpi、xhdpi 和 xxhdpi 这三种屏幕密度的资源->如果23最低版本，启用这行
//        resConfigs("hdpi", "xhdpi", "xxhdpi")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildTypes {
        //测试环境
        debug {
            buildConfigField("boolean", "ISDEBUG", "true")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        //生产环境
        release {
            buildConfigField("boolean", "ISDEBUG", "false")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.android.testing)
    //安卓x库
    api(libs.bundles.androidx.general.core)
    //其余谷歌官方库
    api(libs.bundles.google.extensions)
}