plugins {
    id("java")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")

    implementation(project(":editor:core"))

    // Natives
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-desktop:${LibExt.gdxImGuiVersion}")
    implementation("com.github.xpenatan.gdx-bullet:bullet-desktop:${LibExt.gdxBullet}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
}

val mainClassName = "xlight.editor.Launcher"

tasks.register<JavaExec>("run") {
    group = "Xlight"
    description = "Run Xlight"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}