package com.yeoh.seeker.plugin.utils


import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AccessFlag

import java.util.regex.Matcher
import java.util.regex.Pattern

class GenerateUtils {

    static final String GROUP = "GenerateUtils"

    static int getModifier(CtMethod method, String modifier) {
        if (modifier == null) {
            return -1
        }
        switch (modifier.toLowerCase()) {
            case "default":
                return AccessFlag.setPrivate(method.getModifiers())
            case "public":
                return AccessFlag.setPublic(method.getModifiers())
            case "private":
                return AccessFlag.setPrivate(method.getModifiers())
            case "protected":
                return AccessFlag.setProtected(method.getModifiers())
            default:
                return -1
        }
    }

    static void changeModifier(CtMethod method, String modifier) {
        int targetModifier = getModifier(method, modifier)
        if (targetModifier < 0) {
            return
        }
        method.setModifiers(targetModifier)
    }

    /**
     * 通过方法名称获取方法
     * @param ctClass 方法所在的类
     * @param methodName 　方法名称
     * @param descriptor 　参数 descriptor
     * @return 方法
     */
    static CtMethod getMethod(ClassPool pool, CtClass ctClass, String methodName, String descriptor) {
        if (methodName == null || methodName == "<init>") {
            return null
        }
        CtMethod method = ctClass.getMethod(methodName, descriptor)
        if (method == null) {
            // 获取方法参数
            String reg = "(L.+?;)"
            Pattern pattern = Pattern.compile(reg)
            Matcher matcher = pattern.matcher(descriptor)
            List<String> paramsNames = new ArrayList<>()
            while (matcher.find()) {
                String className = matcher.group()
                className = className.substring(1, className.length() - 1)
                paramsNames.add(className)
            }
            if (!paramsNames.isEmpty()) {
                CtClass[] params = new CtClass[paramsNames.size()]
                for (int i = 0; i < paramsNames.size(); i++) {
                    params[i] = pool.getCtClass(paramsNames[i])
                }
                method = ctClass.getDeclaredMethod(methodName, params)
            }
        }
        return method
    }

    /**
     * 两个类是否相同
     * @param class0 类1
     * @param class1 类2
     * @return 是否相等
     */
    static boolean isClassEqual(String class0, String class1) {
        if (class0 == null || class1 == null) {
            return false
        }
        if (class0 == class1) {
            return true
        }
        if (class0 == class1.replace("\$", ".")) {
            return true
        }
        if (class1 == class0.replace("\$", ".")) {
            return true
        }
        if (class0.replace("\$", ".") == class1.replace("\$", ".")) {
            return true
        }
        return false
    }

    /**
     * 判断两个方法是否相等
     * @param methodName 方法名称
     * @param descriptor 方法 descriptor
     * @param hideMethod HideMethod 实体
     * @return 是否相等
     */
    static boolean equal(String methodName, String descriptor, def hideMethod) {
        if (methodName == null || methodName == "<init>") {
            return false
        }
        if (methodName == null || hideMethod == null) {
            return false
        }
        // 获取参数
        String reg = "(L.+?;)".intern()
        Pattern pattern = Pattern.compile(reg)
        Matcher matcher = pattern.matcher(descriptor)
        List<String> paramsClasses = new ArrayList<>()
        while (matcher.find()) {
            String className = matcher.group()
            className = className.substring(1, className.length() - 1)
            className.replace("/", ".")
            paramsClasses.add(className)
        }
        if (paramsClasses.isEmpty() && hideMethod.params == null) {
            return true
        } else if (hideMethod.params == null) {
            return false
        } else if (!paramsClasses.isEmpty()) {
            return false
        }
        if (paramsClasses.size() != hideMethod.params.size()) {
            return false
        }
        for (int i = 0; i < paramsClasses.size(); i++) {
            if (paramsClasses.get(i) != hideMethod.params[i]) {
                return false
            }
        }
        return true
    }
}