plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "g3d"

dependencies {
    implementation(project(":features:engine:ecs"))
    implementation(project(":features:engine:camera"))
    implementation(project(":features:engine:shaperenderer"))
    implementation(project(":features:engine:math"))
    implementation(project(":features:engine:camera"))
    implementation(project(":features:engine:list"))
    implementation(project(":features:engine:transform"))
    implementation(project(":features:engine:pool"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(LibExt.gdxGLTFVersion)
}