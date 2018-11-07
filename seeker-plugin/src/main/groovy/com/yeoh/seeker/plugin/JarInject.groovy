package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import org.apache.commons.io.FileUtils

class JarInject {

    private static final int LOG_LEVEL = 2
    private static boolean haveTarget = false
    private static final String GROUP = "JarInject"

    static void injectJar(path) throws Exception {
        Log.i(LOG_LEVEL, GROUP, "inject jar")
        if (path.endsWith(".jar")) {
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
        }
        Log.ln(LOG_LEVEL, GROUP)
    }

    private static boolean traverseClassList(List classNameList, String jarZipDir) {
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