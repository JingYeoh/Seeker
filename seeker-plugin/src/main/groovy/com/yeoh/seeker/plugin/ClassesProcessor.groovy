package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.Log
import org.gradle.api.Project

/**
 * 类 和 jar 的处理类
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class ClassesProcessor {

    private static final String GROUP = "ClassesProcessor"
    private static final int LEVEL = 3

    static void processIntoJars(Project project, File jarsDir) {
        for (file in jarsDir.listFiles()) {
            Log.i(LEVEL, GROUP, "file = " + file.path)
        }
    }

    static void processIntoClasses(Project project, File folderOut) {

    }
}