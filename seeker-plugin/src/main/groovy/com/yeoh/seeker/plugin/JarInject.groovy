package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import org.apache.commons.io.FileUtils

class JarInject {

    private static boolean haveTarget = false
    private static final String GROUP = "JarInject"

    static void injectJar(path) throws Exception {
        Log.i(2, GROUP, "inject jar")
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
            File unJar = new File(jarZipDir)
            List classNameList = JarUtils.unJar(jarFile, unJar)

            try {
                boolean haveTarget = traverseClassList(classNameList, jarZipDir)
                if (haveTarget) {
                    Log.i(3, GROUP, "found jar target :" + jarZipDir)
                    jarFile.delete()
                    JarUtils.jar(jarFile, unJar)
                } else {
                    Log.i(3, GROUP, "not found target class")
                }
            } catch (Exception e) {
                throw e
            } finally {
                FileUtils.deleteDirectory(new File(jarZipDir))
            }
        }
        Log.ln(2, GROUP)
    }

    private static boolean traverseClassList(List classNameList, String jarZipDir) {
        haveTarget = false
//        Log.d("DatSource.seekerConfig = " + DataSource.seekerConfig)
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