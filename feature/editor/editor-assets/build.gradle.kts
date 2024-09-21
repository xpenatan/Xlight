plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    implementation(LibExt.gdxGLTFVersion)
}