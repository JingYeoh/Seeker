package com.yeoh.seeker.plugin.processor.bytecode

import javassist.ClassPool

/**
 * 用于注入代码字节码的处理基类
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/13
 */
class BytecodeProcessor {

    protected static ClassPool mClassPool

    /**
     * 设置 ClassPool
     * @param pool ClassPool 对象
     */
    static void setClassPool(ClassPool pool) {
        mClassPool = pool
    }
}