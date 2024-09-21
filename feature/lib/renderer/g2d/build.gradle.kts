plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":feature:lib:transform"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}