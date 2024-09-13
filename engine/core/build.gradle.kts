plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "core"

dependencies {
    api(project(":features:engine:ecs"))
    api(project(":features:engine:asset"))
    api(project(":features:engine:renderer:g3d"))
    api(project(":features:engine:renderer:g2d"))
    api(project(":features:engine:transform"))
    api(project(":features:engine:json"))
    api(project(":features:engine:list"))
    api(project(":features:engine:pool"))
    api(project(":features:engine:math"))
    api(project(":features:engine:datamap"))
    api(project(":features:engine:camera"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}