plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:engine:ecs"))
    implementation(project(":feature:engine:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}