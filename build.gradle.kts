import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
// File: build.gradle.kts (ROOT)
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.5" apply false
    id("jacoco")
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
}

// Konfigurasi Jacoco untuk semua subprojects
        subprojects {
            apply(plugin = "jacoco")
            apply(plugin = "io.gitlab.arturbosch.detekt")

            // Konfigurasi Jacoco
            configure<JacocoPluginExtension> {
                toolVersion = "0.8.11"
            }

            // Konfigurasi Detekt - PERBAIKAN DI SINI
            configure<DetektExtension> {
                config.setFrom(rootProject.files("config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
            }

            // Konfigurasi Detekt tasks
            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(true)
                    sarif.required.set(true)
                }
            }
        }

// Task untuk generate combined coverage report
tasks.register<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { it.tasks.withType<Test>() })

    val sourceDirs = files()
    val classDirs = files()
    val executionData = files()

    subprojects.forEach { project ->
        sourceDirs.from(project.fileTree("src/main/java"))
        classDirs.from(project.fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug"))
        executionData.from(project.fileTree(project.layout.buildDirectory) {
            include("jacoco/testDebugUnitTest.exec")
        })
    }

    sourceDirectories.setFrom(sourceDirs)
    classDirectories.setFrom(classDirs)
    executionData.setFrom(executionData)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}