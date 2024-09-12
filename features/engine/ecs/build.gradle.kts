plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "ecs"

dependencies {
    implementation(project(":features:engine:pool"))
    implementation(project(":features:engine:math"))
    implementation(project(":features:engine:list"))
    implementation(project(":features:engine:camera"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}