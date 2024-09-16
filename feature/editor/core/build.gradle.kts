plugins {
    id("java")
}

dependencies {
    implementation(project(":engine:core"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Frame viewport
    implementation("com.github.xpenatan.gdx-multi-view:core:${LibExt.gdxMultiViewVersion}")
}