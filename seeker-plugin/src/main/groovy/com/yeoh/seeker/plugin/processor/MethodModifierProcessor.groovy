package com.yeoh.seeker.plugin.processor

import com.yeoh.seeker.plugin.DataSource
import com.yeoh.seeker.plugin.utils.GenerateUtils
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AccessFlag
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.Descriptor

/**
 * 添加 @Hide 注解的方法的生成类.
 *
 * 修改方法的 modifier 为指定的 modifier.
 */
class MethodModifierProcessor extends SeekerProcessor {

    private static final String ANNOTATION_HIDE = "com.yeoh.seeker.annotation.Hide"
    private static final int LOG_LEVEL = 3
    static final String GROUP = "MethodModifier"

    /**
     * 处理方法的 modifier
     * @param className 要处理的类
     * @param modifierClass 含有 @Hide 注解的类
     * @param jarZipDir jar 的路径
     * @return 是否处理
     */
    static boolean process(String className, String modifierClass, String jarZipDir) {
        boolean hasTarget = false
        if (GenerateUtils.isClassEqual(className, modifierClass)) {
            startProcess(className, jarZipDir)
            hasTarget = true
        }
        return hasTarget
    }
    /**
     * 开始处理，含有字节码的重新写入
     * @param className 要处理的类
     * @param jarZipDir jar 的路径
     */
    private static void startProcess(String className, String jarZipDir) {
        mClassPool.appendClassPath(jarZipDir)
        CtClass c = mClassPool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        doProcess(c)

        c.writeFile(jarZipDir)
    }
    /**
     * 处理字节码
     * @param c 要处理的类
     */
    private static void doProcess(CtClass c) {
        if (c == null) {
            return
        }
        String className = c.name
        def hideMethods = DataSource.seekerConfig.get(className)
        if (hideMethods == null) {
            return
        }
        Log.i(LOG_LEVEL, GROUP, "begin to process class :" + className)
        Log.i(LOG_LEVEL + 1, GROUP, "hideMethods = " + hideMethods)

        c.setModifiers(AccessFlag.setPublic(c.getModifiers()))

        hideMethods.forEach({
            processTargetMethod(c, it)
        })
        Log.i(LOG_LEVEL, GROUP, "done")
    }

    /**
     * 对添加 @Hide 注解的方法进行处理
     */
    private static void processTargetMethod(CtClass c, def hideMethod) {
        Log.i(LOG_LEVEL + 1, GROUP, " start to change method: " + hideMethod.toString())

        CtClass returns = mClassPool.getCtClass(hideMethod.returns)
        Log.i(LOG_LEVEL + 2, GROUP, "returns get success...")

        CtClass[] params = null
        if (hideMethod.params != null) {
            params = new CtClass[hideMethod.params.size()]
            for (int i = 0; i < params.length; i++) {
                String className = hideMethod.params[i]
                params[i] = mClassPool.getCtClass(className)
            }
        }
        String descriptor = Descriptor.ofMethod(returns, params)
        Log.i(LOG_LEVEL + 2, GROUP, "descriptor get success...")

        CtMethod ctMethod = GenerateUtils.getMethod(mClassPool, c, hideMethod.methodName, descriptor)
        if (ctMethod == null) {
            ThrowExecutionError.throwError(c.name + " not found method:  " + hideMethod.methodName)
        }
        // 改变 modifier 的值
        GenerateUtils.changeModifier(ctMethod, hideMethod.modifier)
        Log.i(LOG_LEVEL + 1, GROUP, c.name + "#" + hideMethod.methodName + " modifier changed to " + hideMethod.modifier)
        // 删除 @Hide 注解
        ctMethod.methodInfo.attributes.forEach({
            if (it instanceof AnnotationsAttribute) {
                for (annotation in it.getAnnotations()) {
                    if (annotation.getTypeName() == ANNOTATION_HIDE) {
                        it.removeAnnotation(ANNOTATION_HIDE)
                        Log.i(LOG_LEVEL + 1, GROUP, c.name + " has removed @Hide")
                    }
                }
            }
        })
    }
}