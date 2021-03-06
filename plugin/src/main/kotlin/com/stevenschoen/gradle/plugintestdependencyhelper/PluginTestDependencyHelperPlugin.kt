package com.stevenschoen.gradle.plugintestdependencyhelper

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class PluginTestDependencyHelperPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val pluginTestMavenRepoDir = project.layout.buildDirectory.dir("pluginTestMaven")

    project.pluginManager.apply("maven-publish")
    with(project.extensions.getByType<PublishingExtension>()) {
      repositories {
        maven {
          name = "pluginTest"
          setUrl(pluginTestMavenRepoDir)
        }
      }
    }

    setupTestHelperSourceGeneration(
      project,
      pluginTestMavenRepoDir = pluginTestMavenRepoDir,
      pluginVersion = project.provider { project.version.toString() },
    )

    val publishTaskName = "publishAllPublicationsToPluginTestRepository"
    project.tasks.withType<Test>().configureEach {
      dependsOn(publishTaskName)
    }
  }

  private fun setupTestHelperSourceGeneration(
    project: Project,
    pluginTestMavenRepoDir: Provider<Directory>,
    pluginVersion: Provider<String>,
  ) {
    val version = this::class.java.classLoader.getResourceAsStream("version")!!
      .use { it.readBytes().decodeToString() }
    project.dependencies {
      "testImplementation"("com.stevenschoen.gradle.plugin-test-dependency-helper:test-sources-api:$version")
    }

    val outputDir = project.layout.buildDirectory.dir("generated/pluginTestHelperTestResources")
    val generateSourcesTaskProvider = project.tasks
      .register<TestHelperResourceGenerationTask>("generatePluginTestHelperResources") {
        pluginTestMavenRepoPathProp.set(pluginTestMavenRepoDir.map { it.asFile.path })
        pluginVersionProp.set(pluginVersion)
        outputDirProp.set(outputDir)
      }
    project.extensions.getByType<JavaPluginExtension>().sourceSets.named("test") {
      resources.srcDir(generateSourcesTaskProvider.flatMap { it.outputDirProp })
    }
  }

}

abstract class TestHelperResourceGenerationTask : DefaultTask() {

  @get:Input
  abstract val pluginTestMavenRepoPathProp: Property<String>

  @get:Input
  abstract val pluginVersionProp: Property<String>

  @get:OutputDirectory
  abstract val outputDirProp: DirectoryProperty

  @TaskAction
  fun generate() {
    val pluginTestMavenRepoPath = pluginTestMavenRepoPathProp.get()
    val pluginVersion = pluginVersionProp.get()
    val outputDir = outputDirProp.get().asFile
    outputDir.deleteRecursively()
    val outputPackageDir = outputDir.resolve("com/stevenschoen/gradle/plugintestdependencyhelper")
    outputPackageDir.mkdirs()

    outputPackageDir.resolve("repositoryDeclaration")
      .writeText("maven { url = uri(\"$pluginTestMavenRepoPath\") }")
    outputPackageDir.resolve("repositoryPath")
      .writeText(pluginTestMavenRepoPath)
    outputPackageDir.resolve("pluginVersion")
      .writeText(pluginVersion)
  }

}
