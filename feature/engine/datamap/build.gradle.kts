plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:engine:json"))
    implementation(project(":feature:engine:lang"))
    implementation(project(":feature:engine:list"))
    implementation(project(":feature:engine:pool"))
    implementation(project(":feature:engine:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":engine:core"))
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}