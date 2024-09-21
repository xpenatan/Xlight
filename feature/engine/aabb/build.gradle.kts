plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:ecs"))
    implementation(project(":feature:lib:aabb:core"))
    implementation(project(":feature:lib:aabb:default"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}