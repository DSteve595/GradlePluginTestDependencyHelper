package com.stevenschoen.gradle.plugintestdependencyhelper

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import java.io.File

class PluginTestDependencyHelperPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val pluginTestMavenRepoDir = project.buildDir.resolve("pluginTestMaven")

    project.pluginManager.apply("maven-publish")
    with(project.extensions.getByType<PublishingExtension>()) {
      repositories {
        maven {
          name = "pluginTest"
          url = project.uri(pluginTestMavenRepoDir)
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
    pluginTestMavenRepoDir: File,
    pluginVersion: Provider<String>,
  ) {
//    val relativePluginTestMavenRepoDir = pluginTestMavenRepoDir.relativeTo(project.projectDir)
    val outputDir = project.layout.buildDirectory.dir("generated/pluginTestHelperTestSources")
    val generateSourcesTaskProvider = project.tasks
      .register<TestHelperSourceGenerationTask>("generatePluginTestHelperSources") {
        pluginTestMavenRepoPathProp.set(pluginTestMavenRepoDir.path)
        pluginVersionProp.set(pluginVersion)
        outputDirProp.set(outputDir)
      }
    project.extensions.getByType<JavaPluginExtension>().sourceSets.named("test") {
      java.srcDir(generateSourcesTaskProvider.flatMap { it.outputDirProp })
    }
  }

}

abstract class TestHelperSourceGenerationTask : DefaultTask() {

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

    outputPackageDir.resolve("PluginTestDependencyHelper.java")
      .writeText(
        """
          package com.stevenschoen.gradle.plugintestdependencyhelper;
          
          public class PluginTestDependencyHelper {
            public static final String repository = "maven { url = uri(\"$pluginTestMavenRepoPath\") }";
            public static final String pluginVersion = "$pluginVersion";
          }
        """.trimIndent()
      )
  }

}
