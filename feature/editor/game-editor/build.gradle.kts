plugins {
    id("java")
}

dependencies {
    implementation(project(":engine:core"))
    implementation(project(":feature:editor:core"))
    implementation(project(":feature:lib::renderer:g3d"))
    implementation(project(":feature:lib::renderer:g2d"))
    implementation(project(":feature:lib::renderer:debug:outline"))
    implementation(project(":feature:editor:editor-assets"))
    implementation(project(":feature:engine:aabb"))
    implementation(project(":feature:lib:aabb:core"))
    implementation(project(":feature:lib:renderer:debug:shaperenderer"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Frame viewport
    implementation("com.github.xpenatan.gdx-multi-view:core:${LibExt.gdxMultiViewVersion}")

    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImGuiVersion}")

    implementation(LibExt.gdxGLTFVersion)
}