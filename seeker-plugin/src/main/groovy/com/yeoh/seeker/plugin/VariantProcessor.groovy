package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.AarUtils
import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import groovy.json.JsonSlurper
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

    static final String PATH_SEEKER_JSON = "./build/Seeker/seeker.json"

    private static final int LEVEL = 2
    private static final String GROUP = "VariantProcessor"
    private final Project mProject
    private final ClassPool mClassPool
    private final def mVariant

    VariantProcessor(Project project, ClassPool classPool, variant) {
        mProject = project
        mClassPool = classPool
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
            String taskPath = 'bundle' + mVariant.name.capitalize()
            Task syncLibTask = mProject.tasks.findByPath(taskPath)
            if (syncLibTask == null) {
                throw new RuntimeException("Can not find task ${taskPath}!")
            }
            syncLibTask.doFirst {
                configureSeeker()
                configureClassPool()
                File dustDir = mProject.file(mProject.buildDir.path + '/intermediates/packaged-classes/' + mVariant.dirName)
                Log.i(LEVEL + 1, GROUP, "outputDir = " + dustDir)
                processJars(dustDir)
            }
        }
    }

    /**
     * 配置 ClassPool，注入依赖的库到 ClassPool 中
     */
    private void configureClassPool() {
        //　解析依赖的第三方库
        DataSource.DEPENDENCIES_PATH.forEach({
            File file = new File(it)
            if (!file.exists()) {
                Log.i(LEVEL + 1, GROUP, "can not find file " + it)
                return
            }
            if (file.path.endsWith("aar")) {
                String extractPath = AarUtils.getExtractAarPath(file)
                DataSource.TEMP_DIRS.add(extractPath)

                File unAar = new File(extractPath)
                AarUtils.Aar aar = AarUtils.unAar(file, unAar)
                mClassPool.appendClassPath(aar.extractJarPath)
                DataSource.DEPENDENCIES_JARS_PATH.add(aar.extractJarPath)

                Log.i(LEVEL + 1, GROUP, "find class jar in aar: " + aar.jarPath)
            }
            if (file.path.endsWith("jar")) {
                String extractJarPath = JarUtils.getExtractJarPath(file)
                DataSource.TEMP_DIRS.add(extractJarPath)
                DataSource.DEPENDENCIES_JARS_PATH.add(extractJarPath)

                JarUtils.unJar(file, new File(extractJarPath))
                mClassPool.appendClassPath(extractJarPath)

                Log.i(LEVEL + 1, GROUP, "find class jar : " + file)
            }
        })
    }

    /**
     * 配置 Seeker ，读取本地 json 文件
     */
    private void configureSeeker() {
        File configFile = new File(PATH_SEEKER_JSON)
        if (configFile.exists()) {
            def content = new StringBuilder()
            configFile.eachLine("UTF-8") {
                content.append(it)
            }
            Map data = new JsonSlurper().parseText(content.toString())
            data.keySet().forEach {
                DataSource.seekerConfig.put(it, data.get(it))
            }
            Log.i(LEVEL, GROUP, "read seeker config success...")
        } else {
            ThrowExecutionError.throwError("seeker.json does not exist")
        }
    }

    /**
     * 处理 jar 文件
     */
    private void processJars(File jarsDir) {
        mClassPool.clearImportedPackages()
        mClassPool.appendClassPath(mProject.android.bootClasspath[0].toString())
        if (jarsDir == null) {
            Log.i(LEVEL, GROUP, "${jarsDir} is not exist")
            return
        }
        for (file in jarsDir.listFiles()) {
            if (file.path.endsWith(".jar")) {
                Log.i(LEVEL + 1, GROUP, "find jar, path = " + file.path)
                mClassPool.appendClassPath(file.absolutePath)
            }
        }
        // inject jar ，重新注入代码
        JarInject jarInject = new JarInject(mClassPool)
        for (file in jarsDir.listFiles()) {
            if (file.path.endsWith(".jar")) {
                jarInject.appendJarPath(file.path)
            }
        }
        jarInject.inject()
    }
}