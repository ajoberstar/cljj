plugins {
  id("dev.clojurephant.clojure")
  id("java-library")
  id("maven-publish")

  id("org.ajoberstar.reckon")
}

group = "org.ajoberstar"

reckon {
  setDefaultInferredScope("patch")
  snapshots()
  setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
  setStageCalc(calcStageFromProp())
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

dependencies {
  api("org.clojure:clojure:1.10.1")
  api("org.clojure:tools.macro:0.1.5")

  testRuntimeOnly("org.ajoberstar:jovial:0.3.0")
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
