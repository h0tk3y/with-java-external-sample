import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library").version("4.2.1")
}

group = "com.h0tk3y"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

android {
    compileSdkVersion = "30"
}

kotlin {
    jvm { withJavaInOtherProject(project(":mpp-lib-java")) }
    js(IR) { browser { } }
    android { }
    ios { }

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

fun KotlinJvmTarget.withJavaInOtherProject(otherProject: Project) {
    otherProject.buildDir = project.buildDir.resolve(otherProject.name)
    otherProject.plugins.apply(JavaPlugin::class.java)
    val javaPluginConvention = otherProject.convention.getPlugin(JavaPluginConvention::class.java)
    val javaSourceSets = javaPluginConvention.sourceSets
    compilations.all {
        val compilation = this@all
        val name = compilation.name
        val javaSrcDir = project.projectDir.resolve("src/${this@withJavaInOtherProject.name}${name.capitalize()}/java")
        compilation.defaultSourceSet.kotlin.srcDir(javaSrcDir)
        val javaSourceSet = javaSourceSets.maybeCreate(name)
        otherProject.tasks.withType<JavaCompile>().named(javaSourceSet.compileJavaTaskName).configure {
            source(javaSrcDir)
        }
        javaSourceSet.compileClasspath = otherProject.files(
            listOf(compilation.compileDependencyFiles, compilation.compileKotlinTaskProvider.map { it.destinationDir })
        )
        compilation.output.classesDirs.from(javaSourceSet.output.classesDirs)
    }
    otherProject.tasks.withType<Test>().all {
        enabled = false
    }
}