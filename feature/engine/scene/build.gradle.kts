plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

dependencies {
    implementation(project(":feature:engine:pool"))

    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:datamap"))
    implementation(project(":feature:lib:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}