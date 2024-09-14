plugins {
    id("java")
    id("java-library")
}

group = "com.xpeengine.imgui"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    implementation(LibExt.gdxGLTFVersion)
}