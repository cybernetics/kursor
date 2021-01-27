plugins {
  kotlin("multiplatform") version "1.4.20"
  `maven-publish`
}

kotlin {
  explicitApi()
  jvm()
  js(BOTH) {
    nodejs()
    browser()
  }
  sourceSets {
    named("commonMain") {
      dependencies {
        api(kotlin("stdlib"))
      }
    }
    named("commonTest") {
      dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-annotations-common"))
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(kotlin("test-junit"))
      }
    }
    named("jsTest") {
      dependencies {
        implementation(kotlin("test-js"))
      }
    }
  }
}

publishing {
  publications.withType<MavenPublication> {
    pom {
      name by rootProject.name
      url by "https://github.com/mpetuska/kursor"
      
      licenses {
        license {
          name by "The Apache License, Version 2.0"
          url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
        }
      }
      
      scm {
        connection by "scm:git:git@github.com:mpetuska/kursor.git"
        url by "https://github.com/mpetuska/kursor"
      }
    }
  }
}
