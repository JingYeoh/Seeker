package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import org.gradle.api.Project

/**
 * 处理 Java 源代码的处理器，hook 了 sourcesJar plugin 并 hook 了相关的 java 代码
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/16
 */
class TrickProcessor {

    private static final int LEVEL = 2
    private static final String GROUP = "TrickProcessor"
    private final Project mProject
    private def mSourcesJar

    private static final String TEMP_SOURCES_ROOT = "./build/Seeker/sources/"

    TrickProcessor(Project project) {
        mProject = project
    }

    /**
     * 开始处理
     */
    void process() {
        Log.i(LEVEL, GROUP, "----------- TrickProcessor -----------")
        resolveSourcesJar()
        hookSourcesJarTask()
        processSources()
        Log.ln(LEVEL, GROUP)
    }

    /**
     * 解析 sourceJar task，获取 from 参数
     */
    private void resolveSourcesJar() {
        mSourcesJar = mProject.tasks.findByName("sourcesJar")
        if (mSourcesJar == null) {
            ThrowExecutionError.throwError("can not find sourcesJar task, please check your gradle")
            return
        }
        Log.i(LEVEL + 1, GROUP, "find sourcesJar plugin")
        mSourcesJar.mainSpec.sourcePaths.forEach {
            Log.i(LEVEL + 1, GROUP, "source path = ${it}")
            DataSource.SOURCE_PATHS.add(it)
        }
    }

    /**
     * hook sourcesJar task 并替换 from 路径
     */
    private void hookSourcesJarTask() {
        Log.i(LEVEL + 1, GROUP, "hookSourcesJarTask")
        DataSource.SOURCE_PATHS.forEach({
            mSourcesJar.mainSpec.sourcePaths.remove(it)
            if (it as String) {

            }
            if (it.startsWith("[") && it.endsWith("]")) {
                Set<String> innerDir = []
                it.forEach { innerIt ->
                    def tempSourcePath = getTempSourcePath(it)
                    innerDir.add(tempSourcePath)
                    // 添加至临时文件夹中，执行完毕后需要删除临时文件夹
                    DataSource.TEMP_DIRS.add(tempSourcePath)
                }
                mSourcesJar.mainSpec.sourcePaths.add(innerDir)
            } else {
                def tempSourcePath = getTempSourcePath(it)
                mSourcesJar.mainSpec.sourcePaths.add(tempSourcePath)
                // 添加至临时文件夹中，执行完毕后需要删除临时文件夹
                DataSource.TEMP_DIRS.add(tempSourcePath)
            }
        })
//        tempDirs.forEach {
//            // hook sourcesJar 中的配置
//            mSourcesJar.mainSpec.sourcePaths.add(it)
//            // 添加至临时文件夹中，执行完毕后需要删除临时文件夹
//            if (it.startsWith("[") && it.endsWith("]")) {
//                it.forEach { innerIt ->
//                    DataSource.TEMP_DIRS.add(innerDir)
//                }
//            } else {
//                DataSource.TEMP_DIRS.add(it)
//            }
//        }
        Log.i(LEVEL + 2, GROUP, sourcesJar.mainSpec.sourcePaths)
    }

    /**
     * hook sources 源码
     */
    private void processSources() {
        mSourcesJar.doFirst {
            hookSources()
        }.doLast {
            deleteTempDir()
        }
    }

    /**
     * hook source 文件夹
     */
    private void hookSources() {

    }

    /**
     * 删除临时文件夹
     */
    private void deleteTempDir() {

    }

    /**
     * 返回需要 hook 的临时路径
     * @param path 原路径
     * @return 新的路径
     */
    private String getTempSourcePath(String path) {
        String[] strs = path.split("/")
        return "${TEMP_SOURCES_ROOT}${strs[strs.length - 1]}"
    }
}