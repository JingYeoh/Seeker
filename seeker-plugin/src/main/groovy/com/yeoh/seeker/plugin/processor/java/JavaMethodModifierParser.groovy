package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit

/**
 * hook Java method modifier 的类
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-11-19
 */
class JavaMethodModifierParser extends BaseJavaParser {

    JavaMethodModifierParser(CompilationUnit compilationUnit, File javaPath) {
        super(compilationUnit, javaPath)
    }

    @Override
    void hook() {

    }
}