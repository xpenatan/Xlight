pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":engine:core")
include(":engine:desktop")
include(":engine:web")

include(":editor:core")
include(":editor:desktop")
include(":editor:web")

// Libs

include(":feature:lib:ecs")
include(":feature:lib:renderer:g3d")
include(":feature:lib:renderer:g2d")
include(":feature:lib:renderer:debug:shaperenderer")
include(":feature:lib:renderer:debug:outline")
include(":feature:lib:renderer:gizmo")
include(":feature:lib:camera")
include(":feature:lib:transform")
include(":feature:lib:pool")
include(":feature:lib:list")
include(":feature:lib:math")
include(":feature:lib:lang")
include(":feature:lib:json")
include(":feature:lib:datamap")
include(":feature:lib:properties")
include(":feature:lib:aabb:core")
include(":feature:lib:aabb:default")
include(":feature:lib:string")
include(":feature:lib:imgui:ui:core")
include(":feature:lib:imgui:ui:file-browser")

// Engine features

include(":feature:engine:core")
include(":feature:engine:scene")
include(":feature:engine:asset")
include(":feature:engine:camera")
include(":feature:engine:transform")
include(":feature:engine:pool")
include(":feature:engine:json")
include(":feature:engine:init")
include(":feature:engine:aabb")
include(":feature:engine:renderer:g3d")
include(":feature:engine:renderer:g2d")
include(":feature:engine:physics:box2d")
include(":feature:engine:physics:jolt")

// Editor features
include(":feature:editor:core")
include(":feature:editor:editor-assets")
include(":feature:editor:renderer:imgui")
include(":feature:editor:renderer:window")
include(":feature:editor:renderer:imgui-util")
include(":feature:editor:renderer:imgui-components")
include(":feature:editor:renderer:content-browser")

include(":feature:editor:game-editor") // Classes used in game editor window

// Demos

include(":demos:g3d:basic:core")
include(":demos:g3d:basic:desktop")
include(":demos:g3d:basic:web")


//includeBuild("E:\\Dev\\Projects\\java\\gdx-teavm") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-teavm:backend-teavm")).using(project(":backends:backend-teavm"))
//        substitute(module("com.github.xpenatan.gdx-teavm:asset-loader")).using(project(":extensions:asset-loader"))
////        substitute(module("com.github.xpenatan.gdx-teavm:gdx-freetype-teavm")).using(project(":extensions:gdx-freetype-teavm"))
//    }
//}

//includeBuild("E:\\Dev\\Projects\\java\\gdx-multi-view") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-multi-view:core")).using(project(":multiview:core"))
//        substitute(module("com.github.xpenatan.gdx-multi-view:imgui-window")).using(project(":extensions:imgui-window"))
//    }
//}

//includeBuild("E:\\Dev\\Projects\\java\\gdx-gltf") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-gltf:core")).using(project(":gltf"))
//    }
//}

//includeBuild("E:\\Dev\\Projects\\java\\gdx-imgui") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-core")).using(project(":imgui:imgui-core"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-desktop")).using(project(":imgui:imgui-desktop"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-teavm")).using(project(":imgui:imgui-teavm"))
//        substitute(module("com.github.xpenatan.gdx-imgui:gdx-impl")).using(project(":gdx:gdx-impl"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-ext-core")).using(project(":imgui-ext:ext-core"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-ext-desktop")).using(project(":imgui-ext:ext-desktop"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-ext-teavm")).using(project(":imgui-ext:ext-teavm"))
//    }
//}

//includeBuild("E:\\Dev\\Projects\\java\\gdx-bullet") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-bullet:bullet-core")).using(project(":bullet:core"))
//        substitute(module("com.github.xpenatan.gdx-bullet:bullet-teavm")).using(project(":bullet:teavm"))
//        substitute(module("com.github.xpenatan.gdx-bullet:bullet-desktop")).using(project(":bullet:desktop"))
//    }
//}