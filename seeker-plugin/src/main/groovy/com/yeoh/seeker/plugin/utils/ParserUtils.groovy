package com.yeoh.seeker.plugin.utils

import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.MethodDeclaration

/**
 * JavaParser 的工具类
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-11-21
 */
class ParserUtils {

    private static final String MODIFIER_NAME = "Modifier"

    /**
     * 返回 方法的 Modifier
     * @param methodDeclaration
     * @return 可空
     */
    static Modifier getMethodModifier(MethodDeclaration methodDeclaration) {
        if (methodDeclaration == null) {
            return null
        }
        def annotations = methodDeclaration.getAnnotations()
        if (annotations == null) {
            return null
        }
        Modifier targetModifier
        for (int i = 0; i < annotations.size(); i++) {
            def annotationExpr = annotations.get(i)
            if (annotationExpr.singleMemberAnnotationExpr) {
                annotationExpr.asSingleMemberAnnotationExpr().getChildNodes().forEach({
                    if (targetModifier == null) {
                        targetModifier = getModifier(it)
                    }
                })
            }
        }
        if (targetModifier == null) {
            targetModifier = Modifier.PRIVATE
        }
        return targetModifier
    }

    /**
     * 返回 方法的 Modifier
     * @param node
     * @return 默认返回 private
     */
    private static Modifier getModifier(Node node) {
        String modifierStr = node.toString()
        if (!modifierStr.startsWith(MODIFIER_NAME)) {
            return null
        }
        modifierStr = modifierStr.substring(MODIFIER_NAME.length() + 1).toLowerCase()
        switch (modifierStr) {
            case "default":
                return Modifier.PRIVATE
            case "public":
                return Modifier.PUBLIC
            case "private":
                return Modifier.PRIVATE
            case "protected":
                return Modifier.PROTECTED
        }
        return Modifier.PRIVATE
    }
}