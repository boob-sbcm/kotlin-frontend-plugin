package org.jetbrains.kotlin.gradle.frontend.webpack

import org.gradle.api.*
import org.jetbrains.kotlin.gradle.frontend.*

/**
 * @author Sergey Mashkov
 */
object WebPackBundler : Bundler<WebPackExtension> {

    override val bundlerId = "webpack"

    override fun createConfig(project: Project) = WebPackExtension(project)

    override fun apply(project: Project, packageManager: PackageManager, bundleTask: Task, runTask: Task, stopTask: Task) {
        packageManager.require(
                listOf("webpack", "webpack-dev-server")
                        .map { Dependency(it, "*", Dependency.DevelopmentScope) }
        )
        if (project.extensions.getByType(KotlinFrontendExtension::class.java).sourceMaps) {
            packageManager.require("source-map-loader")
        }

        val config = project.tasks.create("webpack-config", GenerateWebPackConfigTask::class.java)
        val bundle = project.tasks.create("webpack-bundle", WebPackBundleTask::class.java) { t ->
            t.description = "Bundles all scripts and resources with webpack"
            t.group = WebPackGroup
        }

        bundle.dependsOn(config)

        bundleTask.dependsOn(bundle)
    }

    val WebPackGroup = "webpack"
}
