plugins {
    id("java")
    id("java-library")
}

group = "com.xpeengine.imgui"

dependencies {
    implementation(project(":feature:lib:list"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // ImGui
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-core:${LibExt.gdxImGuiVersion}")
}