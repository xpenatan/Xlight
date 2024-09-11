plugins {
    id("java")
}

group = LibExt.GROUP_ID

val moduleName = "lang"

dependencies {
    implementation(project(":features:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

