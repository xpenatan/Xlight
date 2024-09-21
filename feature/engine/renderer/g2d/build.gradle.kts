plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "g2d"

dependencies {
    implementation(project(":feature:lib:renderer:g2d"))
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:list"))
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}