package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError

/**
 * hook java 源码的基类
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-11-19
 */
abstract class BaseJavaParser {

    protected static final int LEVEL = 4
    protected String GROUP = getClass().getName()

    protected CompilationUnit mCompilationUnit
    protected File mJavaPath
    protected boolean mHasHookTarget

    BaseJavaParser(CompilationUnit compilationUnit, File javaPath) {
        mCompilationUnit = compilationUnit
        mJavaPath = javaPath
    }

    /**
     * 开始执行
     */
    abstract void hook()

    /**
     * 寻找到了 hook 的目标
     */
    protected void findHookTarget() {
        if (!mHasHookTarget) {
            mHasHookTarget = true
        }
    }

    /**
     * 重新写入文件
     */
    protected void writeToPath() {
        if (!mHasHookTarget) {
            return
        }
        if (!mJavaPath.exists()) {
            ThrowExecutionError.throwError("${mJavaPath} does not exist!")
            return
        }
        mJavaPath.write(mCompilationUnit.toString())
        Log.i(LEVEL, GROUP, "write file ${mJavaPath} success")
    }
}