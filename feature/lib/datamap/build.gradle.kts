plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":feature:lib:json"))
    implementation(project(":feature:lib:lang"))
    api(project(":feature:lib:list"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":engine:core"))
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}