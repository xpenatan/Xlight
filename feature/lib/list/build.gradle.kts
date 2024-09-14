plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}