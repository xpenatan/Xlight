plugins {
    id("java")
}

dependencies {
    implementation(project(":engine:core"))
    implementation(project(":feature:lib::renderer:g3d"))
    implementation(project(":feature:editor:renderer:model-util"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Frame viewport
    implementation("com.github.xpenatan.gdx-multi-view:core:${LibExt.gdxMultiViewVersion}")

    implementation(LibExt.gdxGLTFVersion)
}