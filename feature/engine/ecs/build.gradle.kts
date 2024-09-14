plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "ecs"

dependencies {
    implementation(project(":feature:engine:pool"))
    implementation(project(":feature:engine:math"))
    implementation(project(":feature:engine:list"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}