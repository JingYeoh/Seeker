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
    // IDEA 提示的是 java 源码，如果有的话，所以可以直接 hook java 源码
    boolean trickIDEA = true
    // 是否 hook .class 文件
    boolean hookClass = true

    void copy(def seeker) {
        if (seeker == null) {
            return
        }
        enable = seeker.enable
        debugEnable = seeker.debugEnable
        trickIDEA = seeker.trickIDEA
        hookClass = seeker.hookClass
    }

    @Override
    String toString() {
        return "seeker: " +
                " | enable = ${enable}" +
                " | debugEnable = ${debugEnable}" +
                " | trickIDEA = ${trickIDEA}" +
                " | hookClass = ${hookClass}"
    }
}