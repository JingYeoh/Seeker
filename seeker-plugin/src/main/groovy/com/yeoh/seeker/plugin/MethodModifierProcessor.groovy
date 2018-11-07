package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.GenerateUtils
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AccessFlag
import javassist.bytecode.Descriptor

/**
 * 添加 @Hide 注解的方法的生成类.
 *
 * 修改方法的 modifier 为指定的 modifier.
 */
class MethodModifierProcessor {

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
        SeekerTransform.pool.appendClassPath(jarZipDir)
        CtClass c = SeekerTransform.pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        doProcess(c)

        c.writeFile(jarZipDir)
        SeekerTransform.jarClassList.add(c)
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

        CtClass returns = SeekerTransform.pool.getCtClass(hideMethod.returns)
        Log.i(LOG_LEVEL + 2, GROUP, "returns get success...")

        CtClass[] params = null
        if (hideMethod.params != null) {
            params = new CtClass[hideMethod.params.size()]
            for (int i = 0; i < params.length; i++) {
                String className = hideMethod.params[i]
                params[i] = SeekerTransform.pool.getCtClass(className)
            }
        }
        String descriptor = Descriptor.ofMethod(returns, params)
        Log.i(LOG_LEVEL + 2, GROUP, "descriptor get success...")

        CtMethod ctMethod = GenerateUtils.getMethod(c, hideMethod.methodName, descriptor)
        if (ctMethod == null) {
            ThrowExecutionError.throwError(c.name + " not found method:  " + hideMethod.methodName)
        }
        // 改变 modifier 的值
        GenerateUtils.changeModifier(ctMethod, hideMethod.modifier)
        Log.i(LOG_LEVEL + 1, GROUP, c.name + "#" + hideMethod.methodName + " modifier changed to " + hideMethod.modifier)
        // TODO: 删除 @Hide Annotation
    }
}