import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.compose

plugins {
    kotlin("jvm") version "1.4.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.20"
    id("org.jetbrains.compose") version "0.2.0-build132"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

sourceSets.main{
    java.srcDirs("src/main/java", "src/main/kotlin")
}

dependencies {
   // implementation ("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
    implementation ("com.beust:klaxon:5.0.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(compose.desktop.currentOs)

}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}