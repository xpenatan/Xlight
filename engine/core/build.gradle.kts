plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "core"

dependencies {
    api(project(":features:engine:ecs"))
    api(project(":features:engine:renderer:g3d"))
    api(project(":features:engine:renderer:g2d"))
    api(project(":features:engine:transform"))
    api(project(":features:engine:camera"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}