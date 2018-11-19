package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit

/**
 * hook java 方法的类
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-11-19
 */
class JavaReferencedClassParser extends JavaMethodModifierParser {

    JavaReferencedClassParser(CompilationUnit compilationUnit, File javaPath) {
        super(compilationUnit, javaPath)
    }

    @Override
    void hook() {

    }
}