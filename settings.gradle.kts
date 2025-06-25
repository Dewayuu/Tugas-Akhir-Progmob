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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

<<<<<<< HEAD
rootProject.name = "FashionPreloved"
=======
rootProject.name = "TugasAkhirProgmob"
>>>>>>> 3a55271eafa4c5576743b30eb3de6be2275b7383
include(":app")