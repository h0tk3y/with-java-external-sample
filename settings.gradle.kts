pluginManagement {
    fun RepositoryHandler.setup() {
        mavenLocal()
        google()
        jcenter()
        if (this == pluginManagement.repositories) {
            gradlePluginPortal()
        }
    }
    repositories.setup()
    gradle.allprojects { repositories.setup() }

    resolutionStrategy.eachPlugin {
        if (requested.id.id.startsWith("com.android"))
            useModule("com.android.tools.build:gradle:${requested.version}")
    }

    plugins {
        kotlin("multiplatform").version("1.5.10")
    }
}

include("mpp-lib")
include("mpp-lib-java")
