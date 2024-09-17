import java.io.File

plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":demos:g3d:basic:core"))
    api(project(":engine:desktop"))
}

val mainClassName = "xlight.demo.basic.DesktopMain"

tasks.register<JavaExec>("desktop-run") {
    group = "basic-demo"
    description = "Run desktop demo"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = File("../assets_raw/")
}