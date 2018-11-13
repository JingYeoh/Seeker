package com.yeoh.seeker.plugin.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * SeekerExt Task 类，用于在 :Jar 执行之后修改 jar 包.
 */
class Seeker extends DefaultTask {

    @Input
    Boolean release
    @OutputFile
    File destFile

    Seeker() {
        group = 'com.yeoh.plugin'
        description = 'Change class modify and output jar/aar'
    }

    @TaskAction
    void action() {

    }

}