plugins {
    id("java")
    id("java-library")
}

dependencies {
    api(project(":engine:core"))
    implementation(project(":feature:editor:core"))
    implementation(project(":feature:editor:editor-assets"))
    implementation(project(":feature:editor:renderer:imgui"))

    implementation(project(":feature:editor:renderer:imgui"))

    // Libgdx
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}