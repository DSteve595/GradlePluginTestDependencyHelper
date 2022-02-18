### Usage

#### Your plugin's build.gradle
```groovy
plugins {
    id 'com.stevenschoen.gradle.plugin-test-dependency-helper'
}
```
#### Your test's generated settings.gradle
```groovy
pluginManagement.repositories {
    ${PluginTestDependencyHelper.repositoryDeclaration}
    // Other repositories too. For example:
    gradlePluginPortal()
    mavenCentral()
}
```
<details>
    <summary>Alternatively, you can declare the repository manually:</summary>
    ```groovy
    pluginManagement.repositories {
        maven { url = "${PluginTestDependencyHelper.repositoryPath}" }
    }
    ```
</details>

#### Your test's generated build.gradle
```groovy
plugins {
    id("sample") version "${PluginTestDependencyHelper.pluginVersion}"
    // Apply a plugin from an external source. For example:
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}
```

See 'sample' dir
