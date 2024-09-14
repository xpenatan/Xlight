plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "core"

dependencies {
    api(project(":feature:engine:ecs"))
    api(project(":feature:engine:asset"))
    api(project(":feature:engine:renderer:g3d"))
    api(project(":feature:engine:renderer:g2d"))
    api(project(":feature:engine:transform"))
    api(project(":feature:engine:json"))
    api(project(":feature:engine:list"))
    api(project(":feature:engine:pool"))
    api(project(":feature:engine:math"))
    api(project(":feature:engine:datamap"))
    api(project(":feature:engine:camera"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}