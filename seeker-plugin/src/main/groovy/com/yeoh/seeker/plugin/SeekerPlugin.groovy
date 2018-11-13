package com.yeoh.seeker.plugin


import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import groovy.json.JsonSlurper
import javassist.ClassPool
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Seeker 插件
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/8/9
 */
class SeekerPlugin implements Plugin<Project> {

    private Project mProject
    private SeekerExt mSeekerExt
    private static ClassPool mPool

    static final String PATH_SEEKER_JSON = "./build/SeekerExt/seeker.json"

    @Override
    void apply(Project project) {
        mProject = project
        pool = ClassPool.getDefault()

        checkAndroidPlugin()
        configureExtension()

        mProject.afterEvaluate {
            configureSeeker()
            readExtension()
            doAction()
        }
    }

    /**
     *  Make sure the project is either an Android application or library
     */
    private void checkAndroidPlugin() {
        def isAndroidApp = mProject.plugins.withType(AppPlugin)
        def isAndroidLib = mProject.plugins.withType(LibraryPlugin)
        if (!isAndroidApp && !isAndroidLib) {
            throw new GradleException("'com.android.application' or 'com.android.library' plugin required.")
        }
    }

    /**
     * 配置 Extension
     */
    private void configureExtension() {
        mSeekerExt = new SeekerExt()
        mProject.extensions.create(SeekerExt.NAME, SeekerExt)
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
            Log.i(1, GROUP, "read seeker config success...")
        } else {
            ThrowExecutionError.throwError("seeker.json does not exist")
        }
    }

    /**
     * 读取 Extension 配置
     */
    private void readExtension() {
        mSeekerExt.copy(mProject[SeekerExt.NAME])
        Log.d(mSeekerExt.toString())
        Log.Debug = mSeekerExt.debugEnable
    }

    /**
     * 开始执行
     */
    private void doAction() {
        if (!mSeekerExt.enable) {
            Log.d("SeekerExt Plugin is no enabled !")
            return
        }
        Log.d("-------------- SEEKER PLUGIN --------------")

        // 一般有 debug 和 release 两种 variant
        mProject.android.libraryVariants.all { variant ->
            processVariant(variant)
        }
    }
    /**
     * 开始执行处理过程
     */
    private void processVariant(variant) {
        def processor = new VariantProcessor(mProject, mPool, variant)
        processor.processVariant()
    }
}