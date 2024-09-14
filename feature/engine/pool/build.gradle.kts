plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:list"))
    implementation(project(":feature:lib:datamap"))
    implementation(project(":feature:lib:properties"))
    implementation(project(":feature:lib:lang"))
    implementation(project(":feature:lib:json"))

    implementation(project(":feature:engine:json"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}