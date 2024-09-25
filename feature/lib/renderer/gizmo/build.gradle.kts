plugins {
    id("java")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":feature:lib:math"))
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:renderer:g3d"))
    implementation(project(":feature:lib:renderer:debug:shaperenderer"))
}