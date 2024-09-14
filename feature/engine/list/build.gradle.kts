plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}