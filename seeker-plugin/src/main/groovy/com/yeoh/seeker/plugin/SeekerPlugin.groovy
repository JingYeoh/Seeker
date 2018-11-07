package com.yeoh.seeker.plugin

import com.android.build.gradle.AppExtension
import com.yeoh.seeker.plugin.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

class SeekerPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Log.d("-------------- SEEKER PLUGIN --------------")
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new SeekerTransform(project))
    }
}