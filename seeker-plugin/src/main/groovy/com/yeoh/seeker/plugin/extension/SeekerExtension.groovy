package com.yeoh.seeker.plugin.extension
/**
 * SeekerExtension 的配置类
 */
class SeekerExtension {

    public static final String NAME = 'seeker'
    boolean enable = true
    boolean debugEnable = false

    void copy(def seeker) {
        if (seeker == null) {
            return
        }
        enable = seeker.enable
        debugEnable = seeker.debugEnable
    }

    @Override
    String toString() {
        return "seeker: " + "enable = " + enable + " | " + "debugEnable = " + debugEnable
    }
}