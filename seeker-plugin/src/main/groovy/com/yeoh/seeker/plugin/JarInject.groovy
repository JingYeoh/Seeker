package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import javassist.CtClass
import org.apache.commons.io.FileUtils

class JarInject {

    static void injectJar(path) throws Exception {
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
            File unJar = new File(jarZipDir)
            List classNameList = JarUtils.unJar(jarFile, unJar)

            try {
                boolean haveTarget = traverseClassList(classNameList, jarZipDir)
                if (haveTarget) {
                    jarFile.delete()
                    JarUtils.jar(jarFile, unJar)
                }
            } catch (Exception e) {
                throw e
            } finally {
                FileUtils.deleteDirectory(new File(jarZipDir))
            }
        }
    }

    private static boolean traverseClassList(List classNameList, String jarZipDir) {
        boolean haveTarget = false
        boolean hasAppend = false
        for (String className : classNameList) {
            if (className.endsWith(".class")
                    && !className.contains('R$')
                    && !className.contains('R.class')
                    && !className.contains("BuildConfig.class")) {
                className = className.substring(0, className.length() - 6)
                for (String todo : DataSource.seekerConfig.keySet()) {
                    if (className == todo || className.replace("\$", ".") == todo) {
                        Log.d("traverseClassList className = " + className)
                        if (!hasAppend) {
                            hasAppend = true
                            SeekerTransform.pool.appendClassPath(jarZipDir)
                        }
                        haveTarget = true
                        processClass(className, jarZipDir)
                    }
                }
            }
        }
        return haveTarget
    }

    private static void processClass(String className, String path) {
        CtClass c = SeekerTransform.pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        HideMethodProcessor.processHideMethodClass(c, className)

        c.writeFile(path)
        SeekerTransform.jarClassList.add(c)
    }
}