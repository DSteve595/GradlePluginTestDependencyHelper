package sample

import com.stevenschoen.gradle.plugintestdependencyhelper.PluginTestDependencyHelper
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SamplePluginTest {

  @Test
  fun test(@TempDir testProjectDir: File) {
    testProjectDir.resolve("settings.gradle").writeText(
      """
      pluginManagement.repositories {
        ${PluginTestDependencyHelper.repository}
        gradlePluginPortal()
        mavenCentral()
      }
      """.trimIndent()
    )
    testProjectDir.resolve("build.gradle").writeText(
      """
      plugins {
        id("sample") version "${PluginTestDependencyHelper.pluginVersion}"
        // Apply a plugin from an external source
        id("org.jetbrains.kotlin.jvm") version "1.6.10"
      }
      """.trimIndent()
    )
    val result = GradleRunner.create()
      .withProjectDir(testProjectDir)
      .withArguments("samplePluginTask")
      .build()
    assertTrue(result.output.contains("Hello from sample plugin!"))
  }

}
