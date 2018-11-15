package com.yeoh.seeker.plugin.extension
/**
 * SeekerExtension 的配置类
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class SeekerExtension {

    public static final String NAME = 'seeker'
    // 是否启用插件
    boolean enable = true
    // 是否打印日志
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