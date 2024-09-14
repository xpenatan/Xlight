plugins {
    id("java")
}

group = LibExt.GROUP_ID

val moduleName = "ecs"

dependencies {
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:math"))
    implementation(project(":feature:lib:list"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}