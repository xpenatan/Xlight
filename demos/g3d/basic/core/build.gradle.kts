plugins {
    id("java-library")
}

dependencies {
    implementation(project(":engine:core"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}