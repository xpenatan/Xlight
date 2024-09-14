plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:json"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}