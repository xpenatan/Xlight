plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:renderer:shaperenderer"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}