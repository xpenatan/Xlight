plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:math"))
    implementation(project(":feature:lib:renderer:g3d"))
    implementation(project(":feature:lib:renderer:g2d"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}