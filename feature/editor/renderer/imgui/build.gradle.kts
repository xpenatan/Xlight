plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "imgui"

dependencies {
    implementation(project(":engine:core"))

    implementation(project(":feature:editor:core"))
    implementation(project(":feature:editor:editor-assets"))

    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:list"))

    implementation(project(":feature:engine:init"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // ImGui
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImGuiVersion}")
    implementation("com.github.xpenatan.gdx-imgui:gdx-impl:${LibExt.jDearImguiGdxVersion}")
}