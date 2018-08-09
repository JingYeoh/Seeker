package com.yeoh.seeker.plugins

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SeekerPlugins implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new SeekerTransform(project))
    }
}