### Usage

#### Your plugin's build.gradle
```groovy
plugins {
    id 'com.stevenschoen.gradle.plugintestdependencyhelper'
}
```
#### Your test's generated settings.gradle
```groovy
pluginManagement.repositories {
    ${PluginTestDependencyHelper.repository}
    // Other repositories too. For example:
    gradlePluginPortal()
    mavenCentral()
}
```
#### Your test's generated build.gradle
```groovy
plugins {
    id("sample") version "${PluginTestDependencyHelper.pluginVersion}"
    // Apply a plugin from an external source. For example:
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}
```

See 'sample' dir