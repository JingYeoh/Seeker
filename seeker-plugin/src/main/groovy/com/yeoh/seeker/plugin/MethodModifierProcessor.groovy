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

    static final String GROUP = "MethodModifier"

    static void process(CtClass c) {
        if (c == null) {
            return
        }
        String className = c.name
        def hideMethod = DataSource.seekerConfig.get(className)
        if (hideMethod == null) {
            return
        }
        Log.i(2, GROUP, "begin to process class :" + className)
        Log.i(3, GROUP, "hideMethods = " + hideMethod)

        c.setModifiers(AccessFlag.setPublic(c.getModifiers()))

        hideMethod.forEach({
            processTargetMethod(c, it)
        })
        Log.i(2, GROUP, "done")
    }

    /**
     * 对添加 @Hide 注解的方法进行处理
     */
    private static void processTargetMethod(CtClass c, def hideMethod) {
        Log.i(3, GROUP, " start to change method: " + hideMethod.toString())

        CtClass returns = SeekerTransform.pool.getCtClass(hideMethod.returns)
        Log.i(4, GROUP, "returns get success...")

        CtClass[] params = null
        if (hideMethod.params != null) {
            params = new CtClass[hideMethod.params.size()]
            for (int i = 0; i < params.length; i++) {
                String className = hideMethod.params[i]
                params[i] = SeekerTransform.pool.getCtClass(className)
            }
        }
        String descriptor = Descriptor.ofMethod(returns, params)
        Log.i(4, GROUP, "descriptor get success...")

        CtMethod ctMethod = c.getMethod(hideMethod.methodName, descriptor)
        if (ctMethod == null) {
            ctMethod = c.getDeclaredMethod(hideMethod.methodName, params)
        }
        if (ctMethod == null) {
            ThrowExecutionError.throwError(c.name + " not found method:  " + hideMethod.methodName)
        }
        GenerateUtils.changeModifier(ctMethod, hideMethod.modifier)

        Log.i(3, GROUP, c.name + "#" + hideMethod.methodName + " modifier changed to " + hideMethod.modifier)
    }
}