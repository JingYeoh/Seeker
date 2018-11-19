package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.AnnotationExpr
import com.yeoh.seeker.plugin.utils.Log
import groovy.io.FileType

import java.util.function.Consumer

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
        compilationUnit.findAll(MethodDeclaration.class).stream()
                .forEach(new Consumer<MethodDeclaration>() {
            @Override
            void accept(MethodDeclaration methodDeclaration) {
                //检验是否有@Hide注解
                if (!checkHideAnnotation(methodDeclaration)) {
                    return
                }

                //修改方法
                methodDeclaration.getModifiers().stream()
                        .forEach(new Consumer<Modifier>() {
                    @Override
                    void accept(Modifier modifier) {
                        //todo 需改到modifier指定的权限类型
                        if (modifier == Modifier.PUBLIC) {
                            methodDeclaration.getModifiers().remove(modifier)
                            methodDeclaration.setModifier(Modifier.PRIVATE, true)
                        }
                    }
                })

                //删除注解
                def annotations = methodDeclaration.getAnnotations()
                if (annotations != null) {
                    int count = annotations.size()
                    for (int i = count - 1; i >= 0; i--) {
                        def annotationExpr = annotations.get(i)
                        Log.i(LEVEL, GROUP, "method annotation: " + annotationExpr.getNameAsString())
                        //todo 校验方式需review
                        if (isHideAnnotation(annotationExpr)) {
                            methodDeclaration.getAnnotations().remove(annotationExpr)
                        }
                    }
                }
                Log.i(LEVEL, GROUP, "method string: " + methodDeclaration.getDeclarationAsString())
                Log.i(LEVEL, GROUP, "method annotations: " + methodDeclaration.getAnnotations().toString())
                Log.i(LEVEL, GROUP, "method modifier: " + methodDeclaration.getModifiers().toString())
                Log.i(LEVEL, GROUP, "method type: " + methodDeclaration.type.toString())
                methodDeclaration.childNodes.forEach(new Consumer<Node>() {
                    @Override
                    void accept(Node node) {
                        Log.i(LEVEL, GROUP, "method node: " + node.toString())
                    }
                })
                Log.i(LEVEL, GROUP, "method: " + methodDeclaration.toString())
            }
        })

        //删除导包
        def imports = compilationUnit.imports
        if (imports != null) {
            def size = imports.size()
            for (int i = size - 1; i >= 0; i--) {
                def importDeclaration = imports.get(i)
                //todo 校验方式需review
                if (importDeclaration.toString().contains("Hide") || importDeclaration.toString().contains("Modifier")) {
                    imports.remove(importDeclaration)
                }
            }
        }

        compilationUnit.imports.forEach({
            Log.i(LEVEL + 2, GROUP, it.name)
        })
        Log.ln(LEVEL + 1, GROUP)
        Log.i(LEVEL, GROUP, "modified code: " + compilationUnit.toString())


        if (javaPath.exists()) {
            Log.i(LEVEL, GROUP, "delete old code: " + javaPath.delete())
            Log.i(LEVEL, GROUP, "create new file: " + javaPath.createNewFile())
        }

        if (javaPath.exists()) {
            Log.i(LEVEL, GROUP, "write code start ")
            javaPath.write(compilationUnit.toString())
        } else {
            Log.i(LEVEL, GROUP, "write code failed ")
        }

    }

    private boolean checkHideAnnotation(MethodDeclaration methodDeclaration) {
        boolean hasHideAnnotation = false
        def annotations = methodDeclaration.getAnnotations()
        for (int i = 0; i < annotations.size(); i++) {
            def annotationExpr = annotations.get(i)
            if (isHideAnnotation(annotationExpr)) {
                hasHideAnnotation = true
                break
            }
        }
        return hasHideAnnotation
    }

    /**
     * 是否是 @Hide 注解
     * @param annotationExpr
     * @return
     */
    private boolean isHideAnnotation(AnnotationExpr annotationExpr) {
        annotationExpr.getNameAsString().contains("Hide")
    }
}