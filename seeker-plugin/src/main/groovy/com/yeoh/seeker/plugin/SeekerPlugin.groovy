package com.yeoh.seeker.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.yeoh.seeker.plugin.utils.Log
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class SeekerPlugin implements Plugin<Project> {

    private Project mProject

    @Override
    void apply(Project project) {
        mProject = project

        // Make sure the project is either an Android application or library
        def isAndroidApp = project.plugins.withType(AppPlugin)
        def isAndroidLib = project.plugins.withType(LibraryPlugin)
        if (!isAndroidApp && !isAndroidLib) {
            throw new GradleException("'com.android.application' or 'com.android.library' plugin required.")
        }
        Log.d("-------------- SEEKER PLUGIN --------------")

        def android = project.extensions.findByType(AppExtension)
        if (android == null) {
            android = project.extensions.findByType(LibraryExtension)
        }
        android.registerTransform(new SeekerTransform(project))

        project.afterEvaluate {
            Log.d("after evaluate")
            project.android.libraryVariants.all { variant ->
                processVariant(variant)
            }
        }
    }

    private void processVariant(variant) {
        String taskPath = 'prepare' + variant.name.capitalize() + 'Dependencies'
        Log.d("task path = " + taskPath)
//        Task prepareTask = mProject.tasks.findByPath(taskPath)
//        if (prepareTask == null) {
//            throw new RuntimeException("Can not find task ${taskPath}!")
//        }
    }
}