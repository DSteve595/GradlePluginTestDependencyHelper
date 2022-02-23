package com.stevenschoen.gradle.plugintestdependencyhelper;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;

public class PluginTestDependencyHelper {

    /**
     * A dependency declaration for a local Maven repository containing the plugin under test.
     * Add it as a repository to your test project's <b>settings.gradle</b>.
     * Example:
     * <pre>
     *  pluginManagement.repositories {
     *      ${PluginTestDependencyHelper.repositoryDeclaration}
     *      gradlePluginPortal()
     *      mavenCentral()
     *  }
     * </pre>
     * This repository is generated automatically.
     * <br/>
     * This is similar to {@link #repositoryPath}, but is pre-formatted,
     * and can be used directly in a @{code repositories} block in Groovy (settings.gradle) or
     * KTS (settings.gradle.kts).
     * Use either {@code repositoryDeclaration} or {@code repositoryPath}, not both.
     */
    public static final String repositoryDeclaration = loadResourceString("repositoryDeclaration");

    /**
     * An absolute file path to a local Maven repository containing the plugin under test.
     * Add it as a repository to your test project's <b>settings.gradle</b>.
     * Example:
     * <pre>
     *  pluginManagement.repositories {
     *      maven { url = "${PluginTestDependencyHelper.repositoryPath}" }
     *      gradlePluginPortal()
     *      mavenCentral()
     *  }
     * </pre>
     * This repository is generated automatically.
     * <br/>
     * For a pre-formatted maven dependency declaration, you can instead use
     * {@link #repositoryDeclaration}.
     * Use either {@code repositoryDeclaration} or {@code repositoryPath}, not both.
     */
    public static final String repositoryPath = loadResourceString("repositoryPath");

    /**
     * The version of the plugin under test, taken from the plugin project's `version` property.
     * Use it when applying your plugin in your test project's <b>build.gradle</b>.
     * Example:
     * <pre>
     *  plugins {
     *    id("my-plugin-id") version "${PluginTestDependencyHelper.pluginVersion}"
     *  }
     * </pre>
     */
    public static final String pluginVersion = loadResourceString("pluginVersion");

    private static String loadResourceString(String name) {
        try {
            return IOUtils.resourceToString("/com/stevenschoen/gradle/plugintestdependencyhelper/" + name, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}