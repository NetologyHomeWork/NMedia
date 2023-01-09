plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    val canUseChucker = "CAN_USE_CHUCKER"
    val isLogEnabled = "IS_LOGS_ENABLED"
    val baseUrl = "\"http://10.0.2.2:9999/\""

    compileSdk = 33

    defaultConfig {
        applicationId = "ru.netology.nmedia"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        viewBinding = true
    }

    @Suppress("UnstableApiUsage")
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["usesCleartextTraffic"] = false
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField("boolean", canUseChucker, "false")
            buildConfigField("boolean", isLogEnabled, "false")
        }
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = true
            buildConfigField("String", "BASE_URL", baseUrl)
            buildConfigField("boolean", canUseChucker, "true")
            buildConfigField("boolean", isLogEnabled, "true")
        }
    }
}

dependencies {

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Chucker
    implementation("com.github.chuckerteam.chucker:library:3.5.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    // Navigation
    val navVersion = "2.5.1"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Room
    val roomVersion = "2.4.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt ("androidx.room:room-compiler:$roomVersion")

    // LifeCycle
    val lifecycleVersion = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:30.0.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.android.gms:play-services-base")

    // Hyperion
    releaseImplementation("com.willowtreeapps.hyperion:hyperion-core-no-op:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-core:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-attr:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-build-config:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-crash:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-disk:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-geiger-counter:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-measurement:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-phoenix:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-recorder:0.9.34")
    debugImplementation("com.willowtreeapps.hyperion:hyperion-shared-preferences:0.9.34")

    // Image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("com.google.code.gson:gson:2.9.1")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    val koinVersion = "3.2.0"
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}