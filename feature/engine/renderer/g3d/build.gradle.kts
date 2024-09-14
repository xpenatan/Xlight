plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "g3d"

dependencies {
    implementation(project(":feature:engine:ecs"))
    implementation(project(":feature:engine:camera"))
    implementation(project(":feature:engine:shaperenderer"))
    implementation(project(":feature:engine:math"))
    implementation(project(":feature:engine:camera"))
    implementation(project(":feature:engine:list"))
    implementation(project(":feature:engine:transform"))
    implementation(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(LibExt.gdxGLTFVersion)
}