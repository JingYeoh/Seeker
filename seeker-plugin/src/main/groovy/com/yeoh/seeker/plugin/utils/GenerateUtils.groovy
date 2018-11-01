package com.yeoh.seeker.plugin.utils

import com.yeoh.seeker.plugin.SeekerTransform
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AccessFlag

import java.util.regex.Matcher
import java.util.regex.Pattern

class GenerateUtils {

    static final String TAG = "GenerateUtils"

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
    static CtMethod getMethod(CtClass ctClass, String methodName, String descriptor) {
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
                paramsNames.add(className)
            }
            if (!paramsNames.isEmpty()) {
                CtClass[] params = new CtClass[paramsNames.size()]
                for (int i = 0; i < paramsNames.size(); i++) {
                    params[i] = SeekerTransform.pool.getCtClass(paramsNames[i])
                }
                method = ctClass.getDeclaredMethod(methodName, params)
            }
        }
        return method
    }
}