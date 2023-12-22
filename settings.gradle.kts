pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        maven {
            url = uri("https://jcenter.bintray.com")
        }

    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri("https://jcenter.bintray.com")
        }

    }
}

rootProject.name = "Simple pdf viewer"
include(":app")
