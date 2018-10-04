package com.yeoh.seeker.plugin.utils

import com.google.common.util.concurrent.ExecutionError


class ThrowExecutionError {

    static void throwError(String msg) {
        throw new ExecutionError(msg, new Error(msg))
    }

}
