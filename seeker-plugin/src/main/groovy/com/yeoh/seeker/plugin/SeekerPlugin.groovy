package com.yeoh.seeker.plugin


import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.yeoh.seeker.plugin.extension.SeekerExtension
import com.yeoh.seeker.plugin.utils.Log
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
    private SeekerExtension mSeekerExtension
    private static ClassPool mPool

    @Override
    void apply(Project project) {
        mProject = project
        mPool = ClassPool.getDefault()

        checkAndroidPlugin()
        configureExtension()

        mProject.afterEvaluate {
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
        mSeekerExtension = new SeekerExtension()
        mProject.extensions.create(SeekerExtension.NAME, SeekerExtension)
    }

    /**
     * 读取 Extension 配置
     */
    private void readExtension() {
        mSeekerExtension.copy(mProject[SeekerExtension.NAME])
        Log.d(mSeekerExtension.toString())
        Log.Debug = mSeekerExtension.debugEnable
    }

    /**
     * 开始执行
     */
    private void doAction() {
        if (!mSeekerExtension.enable) {
            Log.d("seeker plugin is not enabled!")
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