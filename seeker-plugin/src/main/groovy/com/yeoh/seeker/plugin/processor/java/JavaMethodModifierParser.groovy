package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.MethodDeclaration
import com.yeoh.seeker.plugin.DataSource
import com.yeoh.seeker.plugin.utils.Log

import java.util.function.Consumer

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
        Log.i(LEVEL, GROUP, "parse java file ${mJavaPath}")
        mCompilationUnit.findAll(MethodDeclaration.class).stream()
                .forEach(new Consumer<MethodDeclaration>() {
            @Override
            void accept(MethodDeclaration methodDeclaration) {
                //检验是否有@Hide注解
                if (!checkHideAnnotation(methodDeclaration)) {
                    return
                }

                Log.i(LEVEL + 1, GROUP, "find @Hide in method ${methodDeclaration.name}")

                hookMethodModifier(methodDeclaration)
                hookMethodAnnotation(methodDeclaration)

                Log.i(LEVEL + 1, GROUP, "method string: " + methodDeclaration.getDeclarationAsString())
                Log.i(LEVEL + 1, GROUP, "method annotations: " + methodDeclaration.getAnnotations().toString())
                Log.i(LEVEL + 1, GROUP, "method modifier: " + methodDeclaration.getModifiers().toString())
                Log.i(LEVEL + 1, GROUP, "method type: " + methodDeclaration.type.toString())

                // todo: 这一段代码是做什么的？
                methodDeclaration.childNodes.forEach(new Consumer<Node>() {
                    @Override
                    void accept(Node node) {
                        Log.i(LEVEL, GROUP, "method node: " + node.toString())
                    }
                })
                Log.i(LEVEL, GROUP, "method: " + methodDeclaration.toString())
            }
        })
        hookClassImports()
        writeToPath()
    }

    /**
     * hook method modifier
     * @param methodDeclaration
     */
    private void hookMethodModifier(MethodDeclaration methodDeclaration) {
        methodDeclaration.getModifiers().stream().forEach(new Consumer<Modifier>() {
            @Override
            void accept(Modifier modifier) {
                //todo 需改到modifier指定的权限类型
                if (modifier == Modifier.PUBLIC) {
                    methodDeclaration.getModifiers().remove(modifier)
                    methodDeclaration.setModifier(Modifier.PRIVATE, true)

                    findHookTarget()
                    Log.i(LEVEL + 1, GROUP, "hook method modifier success")
                }
            }
        })
    }

    /**
     * hook method annotation and remove @Hide
     * @param methodDeclaration
     */
    private void hookMethodAnnotation(MethodDeclaration methodDeclaration) {
        //删除注解
        def annotations = methodDeclaration.getAnnotations()
        if (annotations != null) {
            int count = annotations.size()
            for (int i = count - 1; i >= 0; i--) {
                def annotationExpr = annotations.get(i)
                //todo 校验方式需review
                if (isHideAnnotation(annotationExpr)) {
                    methodDeclaration.getAnnotations().remove(annotationExpr)

                    findHookTarget()
                    Log.i(LEVEL + 1, GROUP, "remove @Hide for method ${methodDeclaration.name}")
                }
            }
        }
    }

    /**
     * hook 类的 import，删除 @Hide　的引用
     */
    private void hookClassImports() {
        def imports = mCompilationUnit.imports
        if (imports != null) {
            imports.forEach({
                //todo 校验方式需review
                if (it.toString() == DataSource.ANNOTATION_HIDE ||
                        it.toString() == DataSource.ENUM_MODIFIER) {
                    imports.remove(importDeclaration)

                    findHookTarget()
                    Log.i(LEVEL + 1, GROUP, "remove import ${it} success")
                }
            })
        }
    }

    /**
     * 检查方法中是否含有 @Hide
     * @param methodDeclaration
     * @return
     */
    private boolean checkHideAnnotation(MethodDeclaration methodDeclaration) {
        boolean hasHideAnnotation = false
        def annotations = methodDeclaration.getAnnotations()
        for (int i = 0; i < annotations.size(); i++) {
            def annotationExpr = annotations.get(i)
            annotationExpr.lis
            if (annotationExpr.nameAsString == DataSource.ANNOTATION_HIDE) {
                hasHideAnnotation = true
                break
            }
        }
        return hasHideAnnotation
    }
}