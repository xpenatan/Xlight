plugins {
    id("java")
}

dependencies {
    implementation(project(":feature:lib:aabb:core"))
    implementation(project(":feature:lib:pool"))
    implementation(project(":feature:lib:transform"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}