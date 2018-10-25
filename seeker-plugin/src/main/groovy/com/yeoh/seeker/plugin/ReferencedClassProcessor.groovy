package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.Log
import javassist.CtClass

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 用于修改「调用添加 @Hide 注解的方法」的类.
 *
 * 修改调用逻辑，把原来的正常调用的方法换为生成的通过反射调用的方法.
 */
class ReferencedClassProcessor {

    static void process(CtClass c, String className) {
        if (c == null || className == null) {
            return
        }
        def hideMethods = DataSource.seekerConfig.get(className)
        if (hideMethods == null) {
            return
        }
        Log.d("======== start to process referenced class :" + className)

        // 遍历类中的所有方法，获取方法中涉及到的类，然后和 HideMethod 进行对比
        c.classFile.getMethods().forEach({
            extractClassNames(it.descriptor, hideMethods)
        })

        Log.d("======== done")
    }

    private static void extractClassNames(String descriptor, def hideMethods) {
        String reg = "(L.+?;)"
        Pattern pattern = Pattern.compile(reg)
        Matcher matcher = pattern.matcher(descriptor)
        while (matcher.find()) {
            String className = matcher.group()
            className = className.substring(1, className.length() - 1)
            className = getClassName(className)
            findInSeeker(className, hideMethods)
        }
    }

    private static void findInSeeker(String className, def hideMethods) {
        hideMethods.forEach({
            if(className==hideMethods.)
        })
    }

    private static String getClassName(String className) {
        return className.replaceAll("/", ".")
    }
}