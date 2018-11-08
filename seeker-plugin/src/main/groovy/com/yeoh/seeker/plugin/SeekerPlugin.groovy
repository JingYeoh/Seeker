package com.yeoh.seeker.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.yeoh.seeker.plugin.utils.Log
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

class SeekerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        // Make sure the project is either an Android application or library
        def isAndroidApp = project.plugins.withType(AppPlugin)
        def isAndroidLib = project.plugins.withType(LibraryPlugin)
        if (!isAndroidApp && !isAndroidLib) {
            throw new GradleException("'com.android.application' or 'com.android.library' plugin required.")
        }
        Log.d("-------------- SEEKER PLUGIN --------------")

        project.afterEvaluate {
            project.tasks.all {
                if (it instanceof Jar) {
                    Log.d("task:" + it.getClass().getName())
                }
            }
            project.android.registerTransform(new SeekerTransform(project))
        }
    }
}