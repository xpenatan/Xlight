plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}