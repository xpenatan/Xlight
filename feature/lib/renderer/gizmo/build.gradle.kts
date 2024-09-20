plugins {
    id("java")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation(LibExt.gdxGLTFVersion)

//    implementation(project(":engine:utils:renderer"))
//    implementation(project(":engine:utils:shaperenderer"))
//    implementation(project(":engine:utils:debugrenderer"))
    implementation(project(":feature:lib:math"))
    implementation(project(":feature:lib:renderer:g3d"))
    implementation(project(":feature:lib:renderer:debug:shaperenderer"))
}