plugins {
    id("java")
    id("java-library")
}

group = LibExt.GROUP_ID

val moduleName = "jolt"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}