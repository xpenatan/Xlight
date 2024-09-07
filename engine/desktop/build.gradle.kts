plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "core"

dependencies {
    api(project(":engine:core"))
    api("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    api("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
}