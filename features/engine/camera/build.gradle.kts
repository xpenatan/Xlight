plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "camera"

dependencies {
    implementation(project(":features:engine:ecs"))
    implementation(project(":features:engine:shaperenderer"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}