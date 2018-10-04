package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.GenerateUtils
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.AccessFlag
import javassist.bytecode.Descriptor

class HideMethodProcessor {

    static void processHideMethodClass(CtClass c, String className) {
        if (c == null || className == null) {
            return
        }
        def hideMethod = DataSource.seekerConfig.get(className)
        if (hideMethod == null) {
            return
        }
        Log.d("======== start to process class :" + className)

        c.setModifiers(AccessFlag.setPublic(c.getModifiers()))

        DataSource.seekerConfig.forEach({ key, value ->
            value.forEach({
                processTargetMethod(c, it)
            })
        })
        Log.d("======== done")
    }

    static void processTargetMethod(CtClass c, def hideMethod) {
        Log.d("-------- start to change method: " + hideMethod.toString())
//        CtClass returns = CtClass.forName(hideMethod.returns)
        CtClass returns = SeekerTransform.pool.getCtClass(hideMethod.returns)
        Log.d("returns = " + returns)
        CtClass[] params = null
        if (hideMethod.params != null) {
            params = new CtClass[hideMethod.length]
            for (int i = 0; i < params.length; i++) {
                String className = hideMethod.params[i]
                params[i] = SeekerTransform.pool.getCtClass(className)
            }
        }
        Log.d("params = " + params)
        String descriptor = Descriptor.ofMethod(returns, params)
        Log.d("descriptor = " + descriptor)
        CtMethod ctMethod = c.getMethod(hideMethod.methodName, descriptor)
        if (ctMethod == null) {
            ctMethod = c.getDeclaredMethod(hideMethod.methodName, params)
        }
        if (ctMethod == null) {
            ThrowExecutionError.throwError(c.name + " not found method:  " + hideMethod.methodName)
        }
        GenerateUtils.changeModifier(ctMethod, hideMethod.modifier)
        Log.d(c.name + "#" + hideMethod.methodName + " modifier changed to " + hideMethod.modifier)
    }
}