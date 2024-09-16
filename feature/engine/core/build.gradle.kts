plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:ecs"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}