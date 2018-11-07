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

    static final String REF_DELEGATE_SUFFIX = "RefDelegate"
    static final String REF_DELEGATE_PREFIX = "_"
    static final String GROUP = "ReferencedClass"

    /**
     * 处理调用被 @Hide 注解标记的方法的类
     * @param className 要处理的类
     * @param targetClass 含有 @Hide 注解的类
     */
    static boolean process(String className, String referencedClass, String jarZipDir) {
        // 如果处理的类就是「反射缓存类」，则忽略
        if (GenerateUtils.isClassEqual(className, generateRefDelegateClassName(referencedClass))) {
            return false
        }
        CtClass c = SeekerTransform.pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        for (int i = 0; i < c.refClasses.size(); i++) {
            def it = c.refClasses[i]
            // 判断类是否已经被处理过
            if (GenerateUtils.isClassEqual(it, generateRefDelegateClassName(referencedClass))) {
                Log.i(2, GROUP, "class " + className + " has been proceed")
                startProcess(className, it, jarZipDir, false)
                return false
            }
        }
        boolean hasTarget = false
        // 判断类中是否含有被 @Hide 标记的类
        for (int i = 0; i < c.refClasses.size(); i++) {
            def it = c.refClasses[i]
            if (GenerateUtils.isClassEqual(referencedClass, it)) {
                startProcess(className, it, jarZipDir, true)
                hasTarget = true
            }
        }
        return hasTarget
    }

    /**
     * 开始处理
     * @param hostClass 要处理的类
     * @param referencedClass 含有 @Hide 注解的类
     * @param path jar 包路径
     * @param process 是否对方法进行处理，必须要进行 jar 打包过程，否则已经替换过的会不生效
     */
    private static void startProcess(String hostClass, String referencedClass, String path, boolean process) {
        Log.i(2, GROUP, "start process " + hostClass)
        CtClass host = SeekerTransform.pool.getCtClass(hostClass)
        if (host.isFrozen()) {
            host.defrost()
        }
        if (process) {
            doProcess(host, referencedClass)
        }
        host.writeFile(path)
        SeekerTransform.jarClassList.add(host)
        Log.i(2, GROUP, "process end ...")
        Log.ln(2, GROUP)
    }

    /**
     * 处理过程
     * @param c 要处理的类 CtClass 对象
     * @param referencedClass 含有 @Hide 注解的类
     */
    private static void doProcess(CtClass c, String referencedClass) {
        if (c == null) {
            return
        }
        String hostClass = c.name
        Log.i(3, GROUP, "start to process referenced class :" + hostClass)

        // 遍历类中的所有方法，获取方法中涉及到的类，然后和 HideMethod 进行对比
        c.classFile.getMethods().forEach({
            extractReferencedClassFromMethod(c, referencedClass, it)
        })

        Log.i(3, GROUP, "done")
        Log.ln(3, GROUP)
    }

    /**
     * 从方法中解析出方法涉及到的类
     * @param c 要处理的类 CtClass 对象
     * @param referencedClassName 含有 @Hide 注解的类名
     * @orram info 方法的信息
     */
    private static void extractReferencedClassFromMethod(CtClass ctClass, String referencedClassName, MethodInfo info) {
        String methodName = info.name
        // 通过 descriptor　获取方法参数中的类
        String descriptor = info.descriptor
        Log.i(4, GROUP, "methodName = " + methodName)
        CtMethod ctMethod = GenerateUtils.getMethod(ctClass, methodName, descriptor)
        Log.i(4, GROUP, "method = " + ctMethod)

        Log.i(4, GROUP, "findInSeeker start...")
        if (ctMethod != null) {
            ctMethod.instrument(new ExprEditor() {
                @Override
                void edit(NewExpr e) throws CannotCompileException {
                    findInSeeker(e, referencedClassName)
                }
            })
        }
        Log.i(4, GROUP, "findInSeeker end...")
        Log.ln(4, GROUP)
    }

    /**
     * 在 Seeker 中寻找是否有匹配的类
     * @param e 方法中创建新对象的代码，在此处进行替换为反射代理类
     * @param referencedClassName 含有 @Hide 注解的类名
     */
    private static void findInSeeker(NewExpr e, String referencedClassName) {
        if (referencedClassName != e.className ||
                referencedClassName.replace("\$", ".") != e.className) {
            return
        }
        Log.i(5, GROUP, "find referenced class: " + e.className)
        String replaceBody = "{ " + "\$_ = new " + generateRefDelegateClassName(referencedClassName) + "(\$proceed(\$\$)); " + "}"

        Log.i(5, GROUP, replaceBody)
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
        return packageName + "." + REF_DELEGATE_PREFIX + className + REF_DELEGATE_SUFFIX
    }
}