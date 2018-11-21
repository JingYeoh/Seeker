package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MethodReferenceExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.yeoh.seeker.plugin.DataSource
import com.yeoh.seeker.plugin.utils.Log

import java.util.function.Consumer

/**
 * hook java 方法的类
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-11-19
 */
class JavaReferencedClassParser extends JavaMethodModifierParser {

    private Set<String> mImportHostClasses = []

    JavaReferencedClassParser(CompilationUnit compilationUnit, File javaPath) {
        super(compilationUnit, javaPath)
    }

    @Override
    void hook() {
        Log.i(LEVEL, GROUP, "parse java file ${mJavaPath}")

//        parseImports()
        mCompilationUnit.findAll(MethodDeclaration.class).stream()
                .forEach(new Consumer<MethodDeclaration>() {
            @Override
            void accept(MethodDeclaration methodDeclaration) {
                hookMethodBody(methodDeclaration)
            }
        })

        VoidVisitorAdapter<Object> adapter = new VoidVisitorAdapter<Object>() {
//            @Override
//            void visit(MethodReferenceExpr n, Object arg) {
//                Log.i(LEVEL + 1, GROUP, "refer ${n.name.identifier}")
//            }
//
//            @Override
//            void visit(MethodDeclaration n, Object arg) {
//                Log.i(LEVEL + 1, GROUP, "meth ${n.name.identifier}")
//            }
//
//            @Override
//            void visit(ClassOrInterfaceDeclaration n, Object arg) {
//                Log.i(LEVEL + 1, GROUP, "class ${n.name.identifier}")
//            }

            @Override
            void visit(MethodReferenceExpr n, Object arg) {
                Log.i(LEVEL + 1, GROUP, "class ${n.name.identifier}")
            }
        }
        adapter.visit(mCompilationUnit, null)


        writeToPath()
        Log.ln(LEVEL, GROUP)
    }

    /**
     * 遍历 import 的类，在遍历方法的时候再使用
     */
    private void parseImports() {
        def imports = mCompilationUnit.imports
        if (imports != null) {
            imports.each { importItem ->
                DataSource.seekerConfig.forEach { className, hideMethods ->
                    if (importItem.nameAsString == className) {
                        mImportHostClasses.add(className)
                    }
                }
            }
        }
        Log.i(LEVEL + 1, GROUP, "find host imports: ${mImportHostClasses}")
    }

    /**
     * hook 方法体
     * @param methodDeclaration
     */
    private void hookMethodBody(MethodDeclaration methodDeclaration) {
        VoidVisitorAdapter<Object> adapter = new VoidVisitorAdapter<Object>() {
//            @Override
//            void visit(MethodReferenceExpr n, Object arg) {
//                Log.i(LEVEL + 1, GROUP, "refer ${n.name.identifier}")
//            }
//
            @Override
            void visit(MethodDeclaration n, Object arg) {
                Log.i(LEVEL + 1, GROUP, "meth ${n.name.identifier}")
            }
//
            @Override
            void visit(ClassOrInterfaceDeclaration n, Object arg) {
                Log.i(LEVEL + 1, GROUP, "class ${n.name.identifier}")
            }
        }
        methodDeclaration.accept(adapter,null)
//        adapter.visit(methodDeclaration, null)

//        methodDeclaration.stream().each { lineCode ->
//            processHideHostTarget(lineCode)
//        }
    }

    /**
     * 是否含有持有 @Hide 注解方法的类
     * @param methodDeclaration
     * @return 是否含有
     */
    private boolean processHideHostTarget(String lineCode) {
        def nodes = lineCode.split(" ")
        DataSource.seekerConfig.forEach { className, hideMethods ->
            if (nodes.contains(className)) {
                Log.i(LEVEL + 1, GROUP, "find host target in line ${lineCode}")
            }
        }
    }
}