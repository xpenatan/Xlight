plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":engine:core"))
    implementation(project(":feature:editor:core"))
    implementation(project(":feature:editor:renderer:imgui"))

    implementation(project(":feature:lib:renderer:g3d"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // ImGui
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImGuiVersion}")
    implementation("com.github.xpenatan.gdx-imgui:gdx-impl:${LibExt.jDearImguiGdxVersion}")
}