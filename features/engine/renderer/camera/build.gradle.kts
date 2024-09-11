plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "camera"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}