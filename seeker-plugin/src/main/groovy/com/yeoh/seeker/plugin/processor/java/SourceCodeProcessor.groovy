package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.yeoh.seeker.plugin.utils.Log
import groovy.io.FileType

/**
 * 用于 hook java　源码的处理类
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/18
 */
class SourceCodeProcessor {

    private static final int LEVEL = 3
    private static final String GROUP = "SourceCodeProcessor"

    // 具体到每个类的路径
    private Set<File> mSourcesFilePath = []

    SourceCodeProcessor(def sourcesPath) {
        if (sourcesPath as Collection) {
            sourcesPath.forEach {
                parseJavaSourcePath(it)
            }
        } else if (sourcesPath as String) {
            parseJavaSourcePath(sourcesPath)
        }
    }

    /**
     * 解析文件夹中的文件并且添加到集合中
     * @param folder 文件夹
     */
    private void parseJavaSourcePath(String folder) {
        File root = new File(folder)
        if (!root.exists()) {
            Log.i(LEVEL + 1, GROUP, "${folder} does not exist!")
            return
        }
        root.eachFileRecurse(FileType.FILES) {
            mSourcesFilePath.add(it)
        }
    }

    /**
     * 开始处理
     */
    void process() {
        Log.i(LEVEL, GROUP, "source code processor start...")

        mSourcesFilePath.forEach({
            hookJavaSourceCode(it)
        })

        Log.i(LEVEL, GROUP, "source code processor done")
        Log.ln(LEVEL, GROUP)
    }

    /**
     * 解析 java　源码
     * @param javaPath java　文件路径
     */
    private void hookJavaSourceCode(File javaPath) {
        Log.i(LEVEL + 1, GROUP, "hook java source ${javaPath}")
        CompilationUnit compilationUnit = JavaParser.parse(javaPath)
        // hook method body
        JavaReferencedClassParser referencedClassParser = new JavaReferencedClassParser(compilationUnit, javaPath)
        referencedClassParser.hook()
        // hook method modifier
        JavaMethodModifierParser methodModifierParser = new JavaMethodModifierParser(compilationUnit, javaPath)
        methodModifierParser.hook()
    }

}