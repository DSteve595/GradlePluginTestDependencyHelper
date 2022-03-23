package sample

import org.gradle.api.Plugin
import org.gradle.api.Project

internal class SamplePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    project.tasks.register("samplePluginTask") {
      it.doFirst {
        it.logger.quiet("Hello from sample plugin!")
      }
    }
  }

}