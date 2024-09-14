plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "camera"

dependencies {
    implementation(project(":feature:engine:ecs"))
    implementation(project(":feature:engine:transform"))
    implementation(project(":feature:engine:shaperenderer"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}