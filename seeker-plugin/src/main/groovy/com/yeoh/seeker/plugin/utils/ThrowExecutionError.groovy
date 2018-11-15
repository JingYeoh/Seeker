package com.yeoh.seeker.plugin.utils

import com.google.common.util.concurrent.ExecutionError

/**
 * 用于抛出异常
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/10/4
 */
class ThrowExecutionError {

    static void throwError(String msg) {
        throw new ExecutionError(msg, new Error(msg))
    }

}
