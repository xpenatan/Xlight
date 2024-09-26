plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:properties"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:engine:pool"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}