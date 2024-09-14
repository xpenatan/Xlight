plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:camera"))
    implementation(project(":feature:lib:renderer:shaperenderer"))
    implementation(project(":feature:lib:math"))
    implementation(project(":feature:lib:list"))
    implementation(project(":feature:lib:pool"))

    implementation(project(":feature:engine:transform"))
    implementation(project(":feature:engine:camera"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(LibExt.gdxGLTFVersion)
}