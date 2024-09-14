plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "core"

dependencies {
    api(project(":feature:lib:ecs"))
    api(project(":feature:lib:transform"))
    api(project(":feature:lib:json"))
    api(project(":feature:lib:list"))
    api(project(":feature:lib:pool"))
    api(project(":feature:lib:math"))
    api(project(":feature:lib:camera"))
    api(project(":feature:lib:datamap"))
    api(project(":feature:lib:properties"))

    api(project(":feature:engine:asset"))
    api(project(":feature:engine:renderer:g3d"))
    api(project(":feature:engine:renderer:g2d"))
    api(project(":feature:engine:transform"))
    api(project(":feature:engine:camera"))
    api(project(":feature:engine:json"))
    api(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}