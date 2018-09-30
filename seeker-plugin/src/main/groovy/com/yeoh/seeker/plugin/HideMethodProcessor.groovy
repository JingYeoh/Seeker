package com.yeoh.seeker.plugin

import com.yeoh.seeker.plugin.utils.Log
import javassist.CtClass

class HideMethodProcessor {

    static void processHideMethodClass(CtClass ctClass, String className) {
        if (ctClass == null || className == null) {
            return
        }
        def hideMethod = DataSource.seekerConfig.get(className)
        if (hideMethod == null) {
            return
        }
        Log.d("======== start process class :" + className)

    }
}