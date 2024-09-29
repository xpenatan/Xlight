plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:aabb:core"))
    implementation(project(":feature:lib:aabb:default"))
    implementation(project(":feature:lib:transform"))
    implementation(project(":feature:lib:datamap"))
    implementation(project(":feature:engine:transform"))
    implementation(project(":feature:engine:core"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}