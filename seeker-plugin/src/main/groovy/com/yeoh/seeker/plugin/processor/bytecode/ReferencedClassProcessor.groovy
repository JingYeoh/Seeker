package com.yeoh.seeker.plugin.processor.bytecode

import com.yeoh.seeker.plugin.DataSource
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
 * 修改调用逻辑，把原来的正常调用的方法换为生成的通过反射调用的方法.
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/10/4
 */
class ReferencedClassProcessor extends BytecodeProcessor {

    private static final int LOG_LEVEL = 3
    static final String REF_DELEGATE_SUFFIX = "RefDelegate"
    static final String REF_DELEGATE_PREFIX = "_"
    static final String GROUP = "ReferencedClass"

    /**
     * 是否已经处理过该类
     * @param className 要处理的类
     * @param targetClass 含有 @Hide 注解的类
     * @return 是否已经处理过反射缓存
     */
    static boolean isProcessedInCache(String className, String referencedClass, String jarZipDir) {
        // 如果是否已经处理过该类了，则忽略
        if (DataSource.isProcessedRefDelegate(className, referencedClass)) {
            Log.i(LOG_LEVEL, GROUP, "class " + className + " has been proceed logger from memory cache")
            startProcess(className, referencedClass, jarZipDir, false)
            return true
        }
        return false
    }

    /**
     * 处理调用被 @Hide 注解标记的方法的类
     * @param className 要处理的类
     * @param targetClass 含有 @Hide 注解的类
     * @return 是否进行 jar 重新打包
     */
    static boolean process(String className, String referencedClass, String jarZipDir) {
        // 如果处理的类就是「反射缓存类」，则忽略
        if (GenerateUtils.isClassEqual(className, generateRefDelegateClassName(referencedClass))) {
            return false
        }
        // 如果是否已经处理过该类了（在缓存中判断），则忽略
        if (isProcessedInCache(className, referencedClass, jarZipDir)) {
            return true
        }
        CtClass c = mClassPool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }
        // 判断类是否已经被处理过（判断条件：含有「反射缓存代理类」）
        for (int i = 0; i < c.refClasses.size(); i++) {
            def it = c.refClasses[i]
            if (GenerateUtils.isClassEqual(it, generateRefDelegateClassName(referencedClass))) {
                Log.i(LOG_LEVEL, GROUP, "class " + className + " has been proceed")
                startProcess(className, referencedClass, jarZipDir, false)
                return true
            }
        }
        // 判断类中是否含有被 @Hide 标记的类的方法调用
        for (int i = 0; i < c.refClasses.size(); i++) {
            def it = c.refClasses[i]
            // 如果是否已经处理过该类了（在缓存中判断），则忽略，之所以在此处要再次判断是因为：有时候一个类会返回两次（可能是javassist问题）
            if (isProcessedInCache(className, referencedClass, jarZipDir)) {
                continue
            }
            if (GenerateUtils.isClassEqual(referencedClass, it)) {
                startProcess(className, referencedClass, jarZipDir, true)
                // 缓存到内存中
                DataSource.putToRefCache(className, referencedClass)
            }
        }
        return true
    }

    /**
     * 开始处理
     * @param hostClass 要处理的类
     * @param referencedClass 含有 @Hide 注解的类
     * @param path jar 包路径
     * @param process 是否对方法进行处理，必须要进行 jar 打包过程，否则已经替换过的会不生效
     */
    private static void startProcess(String hostClass, String referencedClass, String path, boolean process) {
        Log.i(LOG_LEVEL, GROUP, "start process " + hostClass)
        CtClass host = mClassPool.getCtClass(hostClass)
        if (host.isFrozen()) {
            host.defrost()
        }
        if (process) {
            doProcess(host, referencedClass)
        }
        host.writeFile(path)
        Log.i(LOG_LEVEL, GROUP, "process end ...")
        Log.ln(LOG_LEVEL, GROUP)
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
        Log.i(LOG_LEVEL + 1, GROUP, "start to process referenced class :" + hostClass)

        // 遍历类中的所有方法，获取方法中涉及到的类，然后和 HideMethod 进行对比
        c.classFile.getMethods().forEach({
            extractReferencedClassFromMethod(c, referencedClass, it)
        })

        Log.i(LOG_LEVEL + 1, GROUP, "done")
        Log.ln(LOG_LEVEL + 1, GROUP)
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
        Log.i(LOG_LEVEL + 2, GROUP, "methodName = " + methodName)
        CtMethod ctMethod = GenerateUtils.getMethod(mClassPool, ctClass, methodName, descriptor)
        Log.i(LOG_LEVEL + 2, GROUP, "method = " + ctMethod)

        Log.i(LOG_LEVEL + 2, GROUP, "findInSeeker start...")
        if (ctMethod != null) {
            ctMethod.instrument(new ExprEditor() {
                @Override
                void edit(NewExpr e) throws CannotCompileException {
                    findInSeeker(e, referencedClassName)
                }
            })
        }
        Log.i(LOG_LEVEL + 2, GROUP, "findInSeeker end...")
        Log.ln(LOG_LEVEL + 2, GROUP)
    }

    /**
     * 在 SeekerExtension 中寻找是否有匹配的类
     * @param e 方法中创建新对象的代码，在此处进行替换为反射代理类
     * @param referencedClassName 含有 @Hide 注解的类名
     */
    private static void findInSeeker(NewExpr e, String referencedClassName) {
        if (referencedClassName != e.className ||
                referencedClassName.replace("\$", ".") != e.className) {
            return
        }
        Log.i(LOG_LEVEL + 3, GROUP, "find referenced class: " + e.className)
        String replaceBody = "{ " + "\$_ = new " + generateRefDelegateClassName(referencedClassName) + "(\$proceed(\$\$)); " + "}"

        Log.i(LOG_LEVEL + 3, GROUP, replaceBody)
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