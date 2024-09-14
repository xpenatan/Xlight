plugins {
    id("java")
}

group = LibExt.GROUP_ID

val moduleName = "lang"

dependencies {
    implementation(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

