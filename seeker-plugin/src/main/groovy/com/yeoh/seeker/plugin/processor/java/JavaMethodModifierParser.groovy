package com.yeoh.seeker.plugin.processor.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.yeoh.seeker.plugin.DataSource
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ParserUtils

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

                hookMethodModifier(methodDeclaration)
                hookMethodAnnotation(methodDeclaration)
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
        final Modifier targetModifier = ParserUtils.getMethodModifier(methodDeclaration)
        if (targetModifier == null) {
            return
        }
        methodDeclaration.getModifiers().stream().forEach(new Consumer<Modifier>() {
            @Override
            void accept(Modifier modifier) {
                //todo 需改到modifier指定的权限类型
                switch (modifier) {
                    case Modifier.PUBLIC:
                    case Modifier.PROTECTED:
                    case Modifier.PRIVATE:
                    case Modifier.DEFAULT:
                        methodDeclaration.getModifiers().remove(modifier)
                        methodDeclaration.setModifier(targetModifier, true)
                        findHookTarget()
                        Log.i(LEVEL + 1, GROUP, "hook method ${methodDeclaration.nameAsString} modifier to ${targetModifier}")
                        break
                    default:
                        break
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
                methodDeclaration.getAnnotations().remove(annotationExpr)

                findHookTarget()
                Log.i(LEVEL + 1, GROUP, "remove @Hide for method ${methodDeclaration.name}")
            }
        }
    }

    /**
     * hook 类的 import，删除 @Hide　的引用
     */
    private void hookClassImports() {
        def imports = mCompilationUnit.imports
        // 之所以要套一层，因为 直接在 forEach 中删除元素会打断当前循环... 未确认
        Set seekerImports = []
        if (imports != null) {
            imports.each {
                if (it.nameAsString == DataSource.ANNOTATION_HIDE || it.nameAsString == DataSource.ENUM_MODIFIER) {
                    seekerImports.add(it)
                }
            }
            seekerImports.forEach({
                imports.remove(it)
                Log.i(LEVEL + 1, GROUP, "remove import ${it.nameAsString} success")
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
        // 是否 import @Hide
        boolean isImportHideAnnotation = false
        def imports = mCompilationUnit.imports
        if (imports != null) {
            imports.forEach({
                if (it.nameAsString == DataSource.ANNOTATION_HIDE) {
                    isImportHideAnnotation = true
                }
            })
        }
        // JavaParser 中返回的都是 String，所以可能无法定位到完整包名，所以需要和 import 一起判断
        def annotations = methodDeclaration.getAnnotations()
        for (int i = 0; i < annotations.size(); i++) {
            def annotationExpr = annotations.get(i)
            if (annotationExpr.nameAsString == DataSource.ANNOTATION_HIDE) {
                hasHideAnnotation = true
                break
            } else if (annotationExpr.nameAsString == "Hide" && isImportHideAnnotation) {
                hasHideAnnotation = true
                break
            }
        }
        return hasHideAnnotation
    }
}