plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}