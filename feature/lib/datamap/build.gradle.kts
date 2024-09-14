plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:json"))
    implementation(project(":feature:lib:lang"))
    implementation(project(":feature:lib:list"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:math"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    testImplementation(project(":engine:core"))
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}