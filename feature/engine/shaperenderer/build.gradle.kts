plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:engine:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}