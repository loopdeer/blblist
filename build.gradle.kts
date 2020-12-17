import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.4.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.21"
    id("org.jetbrains.compose") version "0.3.0-build135"
}

group = "me.pop_p"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }}

/*sourceSets.main{
    java.srcDirs("src/main/java", "src/main/kotlin")
}*/

dependencies {
    //implementation ("org.jetbrains.kotlin:kotlin-stdlib")
   // testImplementation ("org.junit.jupiter:junit-jupiter-api:5.6.0")
    //testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
   // implementation ("com.beust:klaxon:5.0.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
   // implementation(kotlin("stdlib-jdk15"))
    implementation(compose.desktop.currentOs)
    //implementation(kotlin("stdlib-jdk8"))

}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions{
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = "BListBeatlist"
        }
    }
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}