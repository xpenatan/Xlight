plugins {
    id("org.gretty") version("3.1.0")
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

dependencies {
    implementation(project(":editor:core"))
    implementation(project(":engine:web"))

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:${LibExt.gdxTeaVMVersion}")
    implementation("com.github.xpenatan.gdx-imgui:imgui-ext-teavm:${LibExt.gdxImGuiVersion}")
    implementation("com.github.xpenatan.gdx-bullet:bullet-teavm:${LibExt.gdxBullet}")
}

val mainClassName = "xlight.editor.WebBuild"

tasks.register<JavaExec>("editor-build") {
    group = "example-teavm"
    description = "Build editor example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("editor-run-teavm") {
    group = "example-teavm"
    description = "Run teavm app"
    val list = listOf("editor-build", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("editor-build")
}