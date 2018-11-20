package com.yeoh.seeker.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.yeoh.seeker.plugin.extension.SeekerExtension
import com.yeoh.seeker.plugin.utils.Log
import javassist.ClassPool
import javassist.NotFoundException
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration

/**
 * Seeker 插件
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/8/9
 */
class SeekerPlugin implements Plugin<Project> {

    private static final int LEVEL = 1
    private static final String GROUP = "SeekerPlugin"

    private Project mProject
    private SeekerExtension mSeekerExtension
    private static ClassPool mPool

    // copy 引入第三方库的依赖，直接使用原声的 api/implementation 等会抛出异常
    // Resolving configuration 'implementation' directly is not allowed
    private List<Configuration> mCopyDependencies

    @Override
    void apply(Project project) {
        mProject = project
        mPool = ClassPool.getDefault()

        checkAndroidPlugin()
        configureExtension()
        configureDependencies()

        mProject.afterEvaluate {
            Log.i(LEVEL, GROUP, "-------------- SEEKER PLUGIN --------------")
            readExtension()
            resolveArtifacts()
            doAction()
        }

        // 上传完毕后删除临时目录
        Task upload = mProject.tasks.findByName("uploadArchives")
        if (upload != null) {
            upload.doLast {
                DataSource.TEMP_DIRS.forEach({
                    "rm -rf ${it}".execute()
                })
            }
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
     * 添加至依赖的集合中
     */
    private void copyDependencies(Configuration configuration) {
        if (configuration == null) {
            return
        }
        Configuration copyConf = null
        try {
            copyConf = mProject.configurations.getByName("${configuration.name}Copy")
        } catch (Exception ignore) {
        }
        if (copyConf == null) {
            copyConf = mProject.configurations.create("${configuration.name}Copy")
        }
        copyConf.visible = false
        copyConf.extendsFrom configuration
        mCopyDependencies.add(copyConf)
    }

    /**
     * 配置依赖的第三方库
     */
    private void configureDependencies() {
        mCopyDependencies = new ArrayList<>()
        copyDependencies(mProject.configurations.getByName("implementation"))
        copyDependencies(mProject.configurations.getByName("api"))
        copyDependencies(mProject.configurations.getByName("compile"))
        copyDependencies(mProject.configurations.getByName("compileOnly"))
        copyDependencies(mProject.configurations.getByName("provided"))
    }

    /**
     * 读取 Extension 配置
     */
    private void readExtension() {
        mSeekerExtension.copy(mProject[SeekerExtension.NAME])
        Log.i(LEVEL + 1, GROUP, mSeekerExtension.toString())
        Log.Debug = mSeekerExtension.debugEnable
    }

    /**
     * 处理引用的 aar 和 jar　包
     */
    private void resolveArtifacts() {
        def set = new HashSet<>()
        mCopyDependencies.forEach({
            it.each {
                Log.i(LEVEL + 1, GROUP, it.path)
                set.add(it.path)
            }
        })
        DataSource.DEPENDENCIES_PATH = Collections.unmodifiableSet(set)
    }

    /**
     * 开始执行
     */
    private void doAction() {
        if (!mSeekerExtension.enable) {
            Log.i(LEVEL + 1, GROUP, "seeker plugin is not enabled!")
            return
        }
        // 处理 java sources 源码
        processTrick()
        // 处理字节码
        processVariant()
    }

    /**
     * 执行处理 source 源码
     */
    private void processTrick() {
        if (!mSeekerExtension.trickIDEA) {
            return
        }
        TrickProcessor processor = new TrickProcessor(mProject, mSeekerExtension.sourcesJarTask)
        processor.process()
    }

    /**
     * 开始执行处理过程
     */
    private void processVariant() throws NotFoundException {
        if (!mSeekerExtension.hookClass) {
            return
        }
        // 一般有 debug 和 release 两种 variant
        try {
            mProject.android.libraryVariants.all { variant ->
                // 处理 task
                def processor = new VariantProcessor(mProject, mPool, variant)
                processor.process()
            }
        } catch (NotFoundException e) {
            Log.i(LEVEL + 1, GROUP, e.message)
        }
    }
}