buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm") version "1.7.21"
    application
}

application.mainClass.set("adventofcode.Day1Kt")

allprojects {
    repositories {
        mavenCentral()
    }
    kotlin {
        explicitApi()
    }
}
