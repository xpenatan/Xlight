plugins {
    id("java")
}

dependencies {
    implementation(project(":features:engine:ecs"))
    implementation(project(":features:engine:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}