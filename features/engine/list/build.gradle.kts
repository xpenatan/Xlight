plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "list"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(LibExt.gdxGLTFVersion)
}