package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.GenerateUtils
import com.yeoh.seeker.plugin.utils.Log
import javassist.CannotCompileException
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.MethodInfo
import javassist.expr.ExprEditor
import javassist.expr.NewExpr

/**
 * 用于修改「调用添加 @Hide 注解的方法」的类.
 *
 * 修改调用逻辑，把原来的正常调用的方法换为生成的通过反射调用的方法.
 */
class ReferencedClassProcessor {

    static final String REFDELEGATE_SUFFIX = "RefDelegate"
    static final String REFDELEGATE_PREFIX = "_"
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

//        Class clazz = SeekerTransform.pool.toClass(c)
//        Log.i(3, GROUP, clazz)
//        for (Method it : clazz.methods) {
//            Log.i(3, GROUP, it.toString())
//        }

        // 遍历类中的所有方法，获取方法中涉及到的类，然后和 HideMethod 进行对比
        c.classFile.getMethods().forEach({
            extractReferencedClassFromMethod(c, referencedClass, it, hideMethods)
        })

        Log.i(2, GROUP, "done")
    }

    /**
     * 从方法中解析出方法涉及到的类
     */
    private static void extractReferencedClassFromMethod(
            CtClass ctClass, String referencedClassName, MethodInfo info, def hideMethods) {
        String methodName = info.name
        // 通过 descriptor　获取方法参数中的类
        String descriptor = info.descriptor
        Log.i(3, GROUP, "methodName = " + methodName)
        CtMethod ctMethod = GenerateUtils.getMethod(ctClass, methodName, descriptor)
        Log.i(3, GROUP, "method = " + ctMethod)

        Log.i(3, GROUP, "findInSeeker start...")
        if (ctMethod != null) {
            ctMethod.instrument(new ExprEditor() {
                @Override
                void edit(NewExpr e) throws CannotCompileException {
                    findInSeeker(e, referencedClassName)
                }
            })
        }
        Log.i(3, GROUP, "findInSeeker end...")
        Log.ln(3, GROUP)
    }

    /**
     * 在 Seeker 中寻找是否有匹配的类
     */
    private static void findInSeeker(NewExpr e, String referencedClassName) {
        if (referencedClassName != e.className ||
                referencedClassName.replace("\$", ".") != e.className) {
            return
        }
        Log.i(4, GROUP, "find referenced class: " + e.className)
//        String replaceBody = "{ " + "\$_ = new " + generateRefDelegateClassName(referencedClassName) + "(\$proceed(\$\$)); " + "}"
        String replaceBody = "{ " + "\$_ = new " + generateRefDelegateClassName(referencedClassName) + "(\$proceed(\$\$)); " + "}"

        Log.i(4, GROUP, replaceBody)
        e.replace(replaceBody)
    }

    /**
     * 生成反射代理类的完整名称
     * @param classFullName 包含报包名的类名
     */
    private static String generateRefDelegateClassName(String classFullName) {
        String[] splitStr = classFullName.split("\\.")
        String className = splitStr[splitStr.length - 1]
        String packageName = classFullName.substring(0, classFullName.length() - className.length() - 1)
        return packageName + "." + REFDELEGATE_PREFIX + className + REFDELEGATE_SUFFIX
    }
}