pluginManagement {
  plugins {
    id("dev.clojurephant.clojure") version("0.7.0")

    id("org.ajoberstar.grgit") version("5.0.0")
    id("org.ajoberstar.reckon") version("0.16.1")
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven {
      name = "Clojars"
      url = uri("https://repo.clojars.org")
    }
  }
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

rootProject.name = "cljj"
