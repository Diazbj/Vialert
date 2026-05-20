pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                // Obtén tu token secreto en https://account.mapbox.com/
                // Agrégalo en ~/.gradle/gradle.properties como: MAPBOX_DOWNLOADS_TOKEN=sk.xxx
                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orElse("").get()
            }
        }
    }
}

rootProject.name = "My Application"
include(":app")
