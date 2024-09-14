plugins {
    id("java")
}

dependencies {
    implementation(project(":engine:core"))
//    implementation(project(":feature:lib:ecs"))
//    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Frame viewport
    implementation("com.github.xpenatan.gdx-multi-view:core:${LibExt.gdxMultiViewVersion}")
}