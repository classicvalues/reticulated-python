/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.41"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    antlr
    idea
}

idea {
    module {
        generatedSourceDirs.add(file("${project.buildDir}/generated-src/antlr/main"))
    }
}

group = "io.github.oxisto"
version = "0.1-SNAPSHOT"

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    antlr("org.antlr:antlr4:4.7.2")

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

tasks.generateGrammarSource {
    outputDirectory = file("${project.buildDir}/generated-src/antlr/main/io/github/oxisto/reticulated/grammar")
    arguments = arguments + listOf("-visitor", "-package", "io.github.oxisto.reticulated.grammar")
}

tasks.named("compileKotlin") {
    dependsOn(":generateGrammarSource")
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/oxisto/reticulated-python")

            credentials {
                username = findProperty("GITHUB_USERNAME") as String?
                password = findProperty("GITHUB_TOKEN") as String?
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}