plugins {
    id("java")
    id("java-library")
}

dependencies {
    api(project(":feature:lib:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}