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
