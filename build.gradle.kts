plugins {
  id("dev.clojurephant.clojure")
  id("java-library")
  id("maven-publish")
}

group = "org.ajoberstar"

// declare that this is a new version of the old ike.cljj coordinates
val defaultCapability = mapOf("group" to project.group, "name" to project.name, "version" to project.version)
val legacyCapability = mapOf("group" to "org.ajoberstar", "name" to "ike.cljj", "version" to project.version)
configurations.configureEach {
  if (isCanBeConsumed()) {
    outgoing {
      capability(defaultCapability)
      capability(legacyCapability)
    }
  }
}

dependencies {
  api("org.clojure:clojure:1.10.1")
  api("org.clojure:tools.macro:0.1.5")

  testRuntimeOnly("dev.clojurephant:jovial:0.4.2")
}

tasks.withType<Test>() {
  useJUnitPlatform()
}

publishing {
  repositories {
    maven {
      name = "Clojars"
      url = uri("https://repo.clojars.org/")
      credentials {
        username = System.getenv("CLOJARS_USER")
        password = System.getenv("CLOJARS_TOKEN")
      }
    }
  }

  publications {
    create<MavenPublication>("main") {
      from(components["java"])

      versionMapping {
        usage("java-api") { fromResolutionOf("runtimeClasspath") }
        usage("java-runtime") { fromResolutionResult() }
      }

      pom {
        name.set(project.name)
        description.set(project.description)
        url.set("https://github.com/ajoberstar/cljj")

        developers {
          developer {
            name.set("Andrew Oberstar")
            email.set("andrew@ajoberstar.org")
          }
        }

        licenses {
          license {
            name.set("MIT License")
            url.set("https://github.com/ajoberstar/cljj/blob/main/LICENSE")
          }
        }

        scm {
          url.set("https://github.com/ajoberstar/cljj")
          connection.set("scm:git:git@github.com:ajoberstar/cljj.git")
          developerConnection.set("scm:git:git@github.com:ajoberstar/cljj.git")
        }
      }
    }
  }
}

// Clojars doesn't support module metadata yet
tasks.withType<GenerateModuleMetadata>() {
  enabled = false
}
