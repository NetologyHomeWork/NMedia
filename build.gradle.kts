// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.2")
        classpath("com.google.gms:google-services:4.3.14")
    }
}

plugins {
    id("com.android.application") version "7.3.0" apply false
    id ("com.android.library") version "7.3.0" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.10" apply false
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}