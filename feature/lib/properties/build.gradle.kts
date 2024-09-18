plugins {
    id("java")
    id("java-library")
}

dependencies {
    api(project(":feature:lib:json"))
    api(project(":feature:lib:list"))
    implementation(project(":feature:lib:lang"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":engine:core"))
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}