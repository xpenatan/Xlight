plugins {
    id("java")
}

dependencies {
    implementation(project(":engine:core"))
    implementation(project(":feature:lib::renderer:g3d"))
    implementation(project(":feature:editor:renderer:model-util"))
    implementation(project(":feature:editor:editor-assets"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Frame viewport
    implementation("com.github.xpenatan.gdx-multi-view:core:${LibExt.gdxMultiViewVersion}")

    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImGuiVersion}")

    implementation(LibExt.gdxGLTFVersion)
}