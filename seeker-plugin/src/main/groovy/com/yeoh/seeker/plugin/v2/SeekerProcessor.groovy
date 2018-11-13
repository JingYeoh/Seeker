package com.yeoh.seeker.plugin

import javassist.ClassPool

/**
 * 用于注入代码的处理基类
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class SeekerProcessor {

    protected static ClassPool mClassPool

    /**
     * 设置 ClassPool
     * @param pool ClassPool 对象
     */
    static void setClassPool(ClassPool pool) {
        mClassPool = pool
    }
}