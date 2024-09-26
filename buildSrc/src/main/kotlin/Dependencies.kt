object LibExt {
    const val GROUP_ID = "xlight"
    val libVersion: String = getVersion("1.0.0", "b1")

    const val gdxVersion = "1.12.1"
    const val gdxBullet = "-SNAPSHOT"

    const val truthVersion = "1.1.4"

    const val gdxImGuiVersion = "-SNAPSHOT"
    const val jDearImguiGdxVersion = "-SNAPSHOT"
    const val gdxMultiViewVersion = "-SNAPSHOT"
    const val gdxTeaVMVersion = "-SNAPSHOT"

    const val gdxGLTFVersion = "com.github.mgsx-dev.gdx-gltf:gltf:2.2.1"
}

private fun getVersion(releaseVersion: String, suffix: String = ""): String {
    val isRelease = System.getenv("RELEASE")
    var libVersion = "${releaseVersion}-SNAPSHOT"
    if(isRelease != null && isRelease.toBoolean()) {
        libVersion = releaseVersion + if(suffix.isNotEmpty()) "-${suffix}" else ""
    }
    System.out.println("Lib Version: " + libVersion)
    return libVersion
}