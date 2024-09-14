plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "camera"

dependencies {
    implementation(project(":feature:lib:camera"))
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:renderer:shaperenderer"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}