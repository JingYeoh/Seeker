package com.yeoh.seeker.plugin.utils

import javassist.CtMethod
import javassist.bytecode.AccessFlag

class GenerateUtils {

    static int getModifier(CtMethod method, String modifier) {
        if (modifier == null) {
            return -1
        }
        switch (modifier.toLowerCase()) {
            case "default":
                return AccessFlag.setPrivate(method.getModifiers())
            case "public":
                return AccessFlag.setPublic(method.getModifiers())
            case "private":
                return AccessFlag.setPrivate(method.getModifiers())
            case "protected":
                return AccessFlag.setProtected(method.getModifiers())
            default:
                return -1
        }
    }

    static void changeModifier(CtMethod method, String modifier) {
        int targetModifier = getModifier(method, modifier)
        if (targetModifier < 0) {
            return
        }
        method.setModifiers(targetModifier)
    }
}