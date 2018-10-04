package com.yeoh.seeker.plugin.utils

import javassist.CtMethod
import javassist.Modifier

class GenerateUtils {

    static int getModifier(String modifier) {
        if (modifier == null) {
            return -1
        }
        switch (modifier.toLowerCase()) {
            case "default":
                return Modifier.PRIVATE
            case "public":
                return Modifier.PUBLIC
            case "private":
                return Modifier.PRIVATE
            case "protected":
                return Modifier.PROTECTED
            default:
                return -1
        }
    }

    static void changeModifier(CtMethod method, String modifier) {
        int targetModifier = getModifier(modifier)
        if (targetModifier < 0) {
            return
        }
        method.setModifiers(targetModifier)
    }
}