package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.Log
import javassist.ClassPool
import javassist.CtClass
import javassist.bytecode.AttributeInfo
import javassist.bytecode.Descriptor
import javassist.bytecode.MethodInfo

import java.lang.reflect.Method
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 用于修改「调用添加 @Hide 注解的方法」的类.
 *
 * 修改调用逻辑，把原来的正常调用的方法换为生成的通过反射调用的方法.
 */
class ReferencedClassProcessor {

    static final String GROUP = "ReferencedClass"

    static void process(CtClass c, String referencedClass) {
        if (c == null) {
            return
        }
        String hostClass = c.name
        def hideMethods = DataSource.seekerConfig.get(referencedClass)
        if (hideMethods == null) {
            return
        }
        Log.i(2, GROUP, "start to process referenced class :" + hostClass)

        Class clazz = SeekerTransform.pool.toClass(c)
        Log.i(3, GROUP, clazz)
        for (Method it : clazz.methods) {
            Log.i(3, GROUP, it.toString())
        }

        // 遍历类中的所有方法，获取方法中涉及到的类，然后和 HideMethod 进行对比
        c.classFile.getMethods().forEach({
            extractReferencedClassFromMethod(it, hideMethods)
        })

        Log.i(2, GROUP, "done")
    }

    /**
     * 从方法中解析出方法涉及到的类
     */
    private static void extractReferencedClassFromMethod(MethodInfo info, def hideMethods) {
        String methodName = info.name
        // 通过 descriptor　获取方法参数中的类
        String descriptor = info.descriptor
        Log.i(3, GROUP, "method = " + methodName)
        String reg = "(L.+?;)"
        Pattern pattern = Pattern.compile(reg)
        Matcher matcher = pattern.matcher(descriptor)
        while (matcher.find()) {
            String className = matcher.group()
            className = className.substring(1, className.length() - 1)
            Log.i(3, GROUP, "found class " + className + " in " + methodName)
//            className = getClassName(className)
            findInSeeker(className, methodName, hideMethods)
        }
        //　获取方法 body　中涉及到的类
        info.constPool.classNames.forEach({
            Log.i(3, GROUP, "found class by constPool " + it + " in " + methodName)
        })
        info.codeAttribute.attributes.forEach({
            Log.i(3, GROUP, "codeAttributes " + ((AttributeInfo) it).name)
        })
    }

    private static void findInSeeker(String className, String methodName, def hideMethods) {
        hideMethods.forEach({
            if (className == it || className.replace("\$", ".") == it) {
                Log.d("find referenced class in " + className + "#" + methodName)
            }
        })
    }

    private static String getClassName(String className) {
        return className.replaceAll("/", ".")
    }
}