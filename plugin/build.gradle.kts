plugins {
  `java-gradle-plugin`
  `kotlin-dsl`
  `maven-publish`
}

gradlePlugin {
  plugins {
    create("plugin-test-dependency-helper") {
      id = "com.stevenschoen.gradle.plugin-test-dependency-helper"
      implementationClass =
        "com.stevenschoen.gradle.plugintestdependencyhelper.PluginTestDependencyHelperPlugin"
    }
  }
}

val generateVersionResourceProvider =
  tasks.register<GenerateVersionResourceTask>("generateVersionResource") {
    versionProp.set(provider { version.toString() })
    outputDirProp.set(layout.buildDirectory.dir("generated/versionResource/"))
  }

java.sourceSets.named("main") {
  resources.srcDir(generateVersionResourceProvider.flatMap { it.outputDirProp })
}

abstract class GenerateVersionResourceTask : DefaultTask() {

  @get:Input
  abstract val versionProp: Property<String>

  @get:OutputDirectory
  abstract val outputDirProp: DirectoryProperty

  @TaskAction
  fun generate() {
    val version = versionProp.get()
    val outputDir = outputDirProp.get().asFile

    outputDir.deleteRecursively()
    outputDir.mkdirs()
    val versionFile = outputDir.resolve("version")
    versionFile.writeText(version)
  }

}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api")
}