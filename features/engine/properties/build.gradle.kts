plugins {
    id("java")
}

dependencies {
    implementation(project(":features:engine:json"))
    implementation(project(":features:engine:lang"))
    implementation(project(":features:engine:list"))
    implementation(project(":features:engine:pool"))
    implementation(project(":features:engine:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":engine:core"))
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}