package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.processor.java.SourceCodeProcessor
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import org.gradle.api.Project
import org.gradle.api.Task

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
    private Task mSourcesJar

    private static String TEMP_SOURCES_ROOT = "build/Seeker/sources/"

    private Set mHookSourcesPath = []
    private Set mSourcesPath = []

    TrickProcessor(Project project) {
        mProject = project
    }

    /**
     * 开始处理
     */
    void process() {
        Log.i(LEVEL, GROUP, "----------- TrickProcessor -----------")
        File rootHookSources = new File(TEMP_SOURCES_ROOT)
        if (!rootHookSources.exists()) {
            rootHookSources.mkdir()
        }
        TEMP_SOURCES_ROOT = rootHookSources.absolutePath
        Log.i(LEVEL, GROUP, TEMP_SOURCES_ROOT)
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
            if (it as String) {
                mSourcesPath.add(it)
            } else if (it as Collection) {
                Set<String> innerSources = []
                it.forEach { innerIt ->
                    innerSources.add(innerIt)
                }
                mSourcesPath.add(innerSources)
            }
        }
    }

    /**
     * hook sourcesJar task 并替换 from 路径
     */
    private void hookSourcesJarTask() {
        mSourcesPath.forEach({
            mSourcesJar.mainSpec.sourcePaths.remove(it)
            if (it as Collection) {
                Set<String> innerDir = []
                it.forEach { innerIt ->
                    def tempSourcePath = getTempSourcePath(innerIt)
                    innerDir.add(tempSourcePath)
                    // 添加至临时文件夹中，执行完毕后需要删除临时文件夹
                    mHookSourcesPath.add(innerDir)
                }
                mSourcesJar.mainSpec.sourcePaths.add(innerDir)
            } else if (it as String) {
                def tempSourcePath = getTempSourcePath(it)
                mSourcesJar.mainSpec.sourcePaths.add(tempSourcePath)
                // 添加至临时文件夹中，执行完毕后需要删除临时文件夹
                mHookSourcesPath.add(tempSourcePath)
            }
        })
        Log.i(LEVEL + 1, GROUP, "hookSourcesJarTask success")
        Log.i(LEVEL + 2, GROUP, "sources path changed to: ${mSourcesJar.mainSpec.sourcePaths}")
    }

    /**
     * hook sources 源码
     */
    private void processSources() {
        mSourcesJar.doFirst {
            hookSources()
        }.doLast {
            deleteHookSourcesDir()
        }
    }

    /**
     * hook source 文件夹
     * 1. 复制原本的 Sources 文件到 hookSources　中
     * 2. hook java 源代码
     */
    private void hookSources() {
        Log.i(LEVEL + 1, GROUP, "hookSources")
        copySourcesToHookSources()
        hookSourcesFiles()
    }

    /**
     * 删除 hook 的临时文件夹
     */
    private void deleteHookSourcesDir() {
        Log.i(LEVEL + 1, GROUP, "deleteHookSourcesDir")
        mHookSourcesPath.forEach({
//            "rm -rf ${it}".execute()
        })
        mHookSourcesPath.clear()
    }

    /**
     * 返回需要 hook 的临时路径
     * @param path 原路径
     * @return 新的路径
     */
    private String getTempSourcePath(def path) {
        path = path.toString()
        String[] arr = path.split("/")
        return "${TEMP_SOURCES_ROOT}/${arr[arr.length - 1]}"
    }

    /**
     * 复制 sources 到 hook　sources 文件夹
     */
    private void copySourcesToHookSources() {
        for (int i = 0; i < mSourcesPath.size(); i++) {
            def source = mSourcesPath[i]
            if (source as Collection) {
                for (int j = 0; j < source.size(); j++) {
                    mProject.copy {
                        from(source)
                        into(mHookSourcesPath[i][j])
                    }
                }
            } else if (source as String) {
                mProject.copy {
                    from(source)
                    into(mHookSourcesPath[i])
                }
            }
        }
        Log.i(LEVEL + 1, GROUP, "copySourcesToHookSources done...")
    }

    /**
     * hook java　源码
     */
    private void hookSourcesFiles() {
        mHookSourcesPath.forEach({
            SourceCodeProcessor processor = new SourceCodeProcessor(it)
            processor.process()
        })
    }
}