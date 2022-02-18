plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("plugin-test-dependency-helper") {
            id = "com.stevenschoen.gradle.plugin-test-dependency-helper"
            implementationClass = "com.stevenschoen.gradle.plugintestdependencyhelper.PluginTestDependencyHelperPlugin"
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api")
}