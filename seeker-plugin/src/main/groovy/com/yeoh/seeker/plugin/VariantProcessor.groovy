package com.yeoh.seeker.plugin


import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.v2.JarInject2
import javassist.ClassPool
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * 具体的处理类，用于处理 Seeker 中各个 plugin 的执行
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class VariantProcessor {

    private static final int LEVEL = 1
    private static final String GROUP = "VariantProcessor"
    private final Project mProject
    private final ClassPool mPool
    private final def mVariant

    VariantProcessor(Project project, ClassPool pool, variant) {
        mProject = project
        mPool = pool
        mVariant = variant
    }

    /**
     * 处理 variant
     */
    void processVariant() {
//        String taskPath = 'prepare' + mVariant.name.capitalize() + 'Dependencies'
//        Task prepareTask = mProject.tasks.findByPath(taskPath)
//        if (prepareTask == null) {
//            throw new RuntimeException("Can not find task ${taskPath}!")
//        }
        Log.i(LEVEL, GROUP, "----------- VariantProcessor ${mVariant.name} -----------")
        processClassesAndJars()
    }

    /**
     * 处理类和 jar 文件
     */
    private void processClassesAndJars() {
        // 如果使用混淆， bundleRelease 必须在混淆之前执行
        if (mVariant.getBuildType().isMinifyEnabled()) {
            Task javacTask = mVariant.getJavaCompile()
            if (javacTask == null) {
                // warn: can not find javaCompile task, jack compile might be on.
                return
            }
            javacTask.doLast {
                File dustDir = mProject.file(mProject.buildDir.path + '/intermediates/classes/' + mVariant.dirName)
                Log.i(LEVEL + 1, GROUP, "minify enable")
                Log.i(LEVEL + 1, GROUP, "dustDir = " + dustDir)
            }
        } else {
            String taskPath = 'transformClassesAndResourcesWithSyncLibJarsFor' + mVariant.name.capitalize()
            Task syncLibTask = mProject.tasks.findByPath(taskPath)
            if (syncLibTask == null) {
                throw new RuntimeException("Can not find task ${taskPath}!")
            }
            syncLibTask.doLast {
                File dustDir = mProject.file(mProject.buildDir.path + '/intermediates/packaged-classes/' + mVariant.dirName)
                Log.i(LEVEL + 1, GROUP, "outputDir = " + dustDir)
                processJars(dustDir)
            }
        }
    }

    /**
     * 处理 jar 文件
     */
    private void processJars(File jarsDir) {
        if (jarsDir == null) {
            Log.i(LEVEL + 1, GROUP, "${jarsDir} is not exist")
            return
        }
        for (file in jarsDir.listFiles()) {
            if (file.path.endsWith(".jar")) {
                Log.i(LEVEL + 2, GROUP, "find jar, path = " + file.path)
                mPool.appendClassPath(file.absolutePath)
            }
        }
        // inject jar ，重新注入代码
        JarInject2 jarInject = new JarInject2(mPool)
        for (file in jarsDir.listFiles()) {
            if (file.path.endsWith(".jar")) {
                jarInject.appendJarPath(file.path)
            }
        }
        jarInject.inject()
    }
}