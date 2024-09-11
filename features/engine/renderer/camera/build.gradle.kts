plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "g3d"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(LibExt.gdxGLTFVersion)
}