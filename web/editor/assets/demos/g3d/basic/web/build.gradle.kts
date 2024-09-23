plugins {
  id("java")
  id("org.gretty") version("3.1.0")
}

gretty {
  contextPath = "/"
  extraResourceBase("build/dist/webapp")
}

val mainClassName = "xlight.demo.basic.WebBuild"

dependencies {
  implementation(project(":demos:g3d:basic:core"))

  implementation(project(":engine:web"))
}

tasks.register<JavaExec>("web-build") {
  group = "basic-demo"
  description = "build teavm"
  mainClass.set(mainClassName)
  classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("web-run") {
  group = "basic-demo"
  description = "Run teavm app"
  val list = listOf("web-build", "jettyRun")
  dependsOn(list)

  tasks.findByName("jettyRun")?.mustRunAfter("web-build")
}