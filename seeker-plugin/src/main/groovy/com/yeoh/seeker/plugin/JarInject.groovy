package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.DataSource
import com.yeoh.seeker.plugin.processor.MethodModifierProcessor
import com.yeoh.seeker.plugin.processor.ReferencedClassProcessor
import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import javassist.ClassPool
import org.apache.commons.io.FileUtils

/**
 * jar 包的注入类
 *
 * 过程
 * * 1. 解析 jar 包
 * * 2. 比对配置
 * * 3. 调用代码注入器
 * * 4. 重新打包
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class JarInject {

    private static final int LOG_LEVEL = 2
    private static final String GROUP = "JarInject"
    private boolean haveTarget = false

    private final ClassPool mClassPool
    private List<String> mJarPaths

    JarInject(ClassPool pool) {
        mClassPool = pool
        mJarPaths = new ArrayList<>()
    }

    /**
     * 添加 Jar 的路径
     * @param path jar 路径
     */
    void appendJarPath(String path) {
        if (!mJarPaths.contains(path)) {
            mJarPaths.add(path)
        }
    }

    /**
     * 开始注入
     */
    void inject() throws Exception {
        MethodModifierProcessor.setClassPool(mClassPool)
        ReferencedClassProcessor.setClassPool(mClassPool)
        mJarPaths.forEach({
            injectJar(it)
        })
    }

    /**
     * 修改 jar 包
     * @param path jar 的路径
     * @throws Exception 可能会抛出异常
     */
    private void injectJar(path) throws Exception {
        Log.i(LOG_LEVEL, GROUP, "inject jar " + path)

        File jarFile = new File(path)
        String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
        File unJar = new File(jarZipDir)
        List classNameList = JarUtils.unJar(jarFile, unJar)

        try {
            boolean haveTarget = traverseClassList(classNameList, jarZipDir)
            if (haveTarget) {
                Log.i(LOG_LEVEL + 1, GROUP, "found jar target :" + jarZipDir)
                jarFile.delete()
                JarUtils.jar(jarFile, unJar)
            } else {
                Log.i(LOG_LEVEL + 1, GROUP, "not found target class")
            }
        } catch (Exception e) {
            throw e
        } finally {
            FileUtils.deleteDirectory(new File(jarZipDir))
        }

        Log.ln(LOG_LEVEL, GROUP)
    }

    /**
     * 对比配置文件，调用代码注入器
     * @param classNameList jar 包中的类集合
     * @param jarZipDir jar zip 的目录
     * @return 该 jar 包中是否注入代码
     */
    private boolean traverseClassList(List classNameList, String jarZipDir) {
        haveTarget = false
        for (String className : classNameList) {
            if (className.endsWith(".class")
                    && !className.contains('R$')
                    && !className.contains('R.class')
                    && !className.contains("BuildConfig.class")) {
                className = className.substring(0, className.length() - 6)

                DataSource.seekerConfig.keySet().forEach({
                    // 寻找是否含有 @Hide 注解的类
                    if (MethodModifierProcessor.process(className, it, jarZipDir)) {
                        haveTarget = true
                    } else {
                        // 含有 @Hide 注解的类不进行反射处理，只处理调用的类
                        boolean result = ReferencedClassProcessor.process(className, it, jarZipDir)
                        // 不可以覆盖上面的查询结果，如果直接赋值的话可能改 jar 包中含有 @Hide 的类无法被打包
                        haveTarget = !haveTarget ? result : haveTarget
                    }
                })
            }
        }
        return haveTarget
    }
}