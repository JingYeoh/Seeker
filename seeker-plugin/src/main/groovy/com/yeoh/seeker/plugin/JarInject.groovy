package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.JarUtils
import com.yeoh.seeker.plugin.utils.Log
import javassist.CtClass
import org.apache.commons.io.FileUtils

class JarInject {

    static void injectJar(path) throws Exception {
        Log.d("---------- inject jar ------------")
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')
            File unJar = new File(jarZipDir)
            List classNameList = JarUtils.unJar(jarFile, unJar)

            try {
                boolean haveTarget = traverseClassList(classNameList, jarZipDir)
                if (haveTarget) {
                    Log.d("found jar target :" + jarZipDir)
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
//        Log.d("DatSource.seekerConfig = " + DataSource.seekerConfig)
        for (String className : classNameList) {
            if (className.endsWith(".class")
                    && !className.contains('R$')
                    && !className.contains('R.class')
                    && !className.contains("BuildConfig.class")) {
                className = className.substring(0, className.length() - 6)

                DataSource.seekerConfig.keySet().forEach({
                    // find hide method
                    if (className == it || className.replace("\$", ".") == it) {
                        if (!hasAppend) {
                            hasAppend = true
                            SeekerTransform.pool.appendClassPath(jarZipDir)
                        }
                        haveTarget = true
                        processClass(it, jarZipDir)
                    } else if (hasReferencedClass(className, it)) {

                    }
                })
            }
        }
        return haveTarget
    }

    private static void processClass(String className, String path) {
        CtClass c = SeekerTransform.pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        MethodModifierProcessor.process(c, className)

        c.writeFile(path)
        SeekerTransform.jarClassList.add(c)
    }

    private static boolean hasReferencedClass(String className, String targetClass) {
        CtClass c = SeekerTransform.pool.getCtClass(className)
        if (c.isFrozen()) {
            c.defrost()
        }

        c.refClasses.forEach({
            if (targetClass == it || targetClass.replace("\$", ".") == it) {
                Log.d("found reference class :" + className)
            }
        })

        return false
    }
}