plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":features:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}