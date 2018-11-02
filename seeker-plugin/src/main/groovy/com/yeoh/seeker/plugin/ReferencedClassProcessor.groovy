package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.GenerateUtils
import com.yeoh.seeker.plugin.utils.Log
import javassist.CannotCompileException
import javassist.CtClass
import javassist.CtMethod
import javassist.bytecode.MethodInfo
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import javassist.expr.NewExpr

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

        if (ctMethod != null) {
            ctMethod.instrument(new ExprEditor() {
                @Override
                void edit(MethodCall m) throws CannotCompileException {
                    findInSeeker(m, referencedClassName, hideMethods)
                }

                @Override
                void edit(NewExpr e) throws CannotCompileException {
                    super.edit(e)
                }
            })
        }
    }

    /**
     * 在 Seeker 配置中寻找方法是否匹配
     */
    private static void findInSeeker(MethodCall m, String referencedClassName, def hideMethods) {
        if (referencedClassName != m.className ||
                referencedClassName.replace("\$", ".") != m.className) {
            return
        }
        Log.i(3, GROUP, "findInSeeker start..." + m.className + "#" + m.methodName)
        Log.i(4, GROUP, "find referenced class " + referencedClassName)

        // freeze referenced class
        CtClass ctClass = SeekerTransform.pool.getCtClass(referencedClassName)
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }
        String descriptor = m.method.getMethodInfo().descriptor
        hideMethods.forEach({
            if (GenerateUtils.equal(m.methodName, descriptor, it)) {
                Log.i(4, GROUP, "find referenced method: " + it)
                getRefBarrierBody(referencedClassName, it)
//                m.replace("{ \$_ = $proceed(\$\$); }")
                m.replace("{}")
            }
        })
        Log.i(3, GROUP, "findInSeeker end...")
    }

    /**
     * 返回需要 replace 的新的 body
     *
     * 被替换的表达式为：xx = new xx$$RefBarrier(xx).xxx(..)
     */
    private static String getRefBarrierBody(String referencedClassName, def hideMethod) {
        StringBuilder builder = new StringBuilder()
        builder.append("{ ")
        // 又返回值需要加上返回参数
        if (hideMethod.returns != null && hideMethod.returns.toLowerCase() != "void") {
            builder.append("\$_ = ")
            // 强制转换参数
            builder.append("(\$r)")
        }
        builder.append("new ")
        builder.append(referencedClassName).append("\$\$RefBarrier")
        builder.append("(")
        builder.append("\$class")
        builder.append(")")
        builder.append(".")
        builder.append(hideMethod.methodName)
        if (hideMethod.params == null) {
            builder.append("();")
        } else {
            builder.append("(\$\$);")
        }
        builder.append(" }")

        Log.i(6, GROUP, builder.toString())
        return builder.toString()
    }

    private static String getClassName(String className) {
        return className.replaceAll("/", ".")
    }
}