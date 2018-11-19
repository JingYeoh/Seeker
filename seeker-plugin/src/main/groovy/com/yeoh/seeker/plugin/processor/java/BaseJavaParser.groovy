package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit

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

    BaseJavaParser(CompilationUnit compilationUnit, File javaPath) {
        mCompilationUnit = compilationUnit
        mJavaPath = javaPath
    }

    /**
     * 开始执行
     */
    abstract void hook()
}