package com.yeoh.seeker.plugin.utils

import java.util.logging.Logger

/**
 * Used to print log info
 *
 * @author JingYeoh
 * @since 2018-08-09
 */
class Log {
    static final String TAG = "seeker"
    public static Debug = true
    public static Logger logger = Logger.getLogger("seeker")

    static void d(Object msg) {
        if (Debug) {
            println("$TAG: " + msg.toString())
        }
    }

    static void i(int level, String group, Object msg) {
        if (Debug) {
            def start = new StringBuffer()
            for (int i = 0; i < getStartSpace(level); i++) {
                start.append(" ")
            }
            for (int i = 0; i < level; i++) {
                start.append(">")
            }
            start.append(" ")
            start.append(group)
            start.append(": ")
            start.append(msg.toString())
            println(start.toString())
        }
    }

    /**
     * 返回开始的空格个数
     */
    private static int getStartSpace(int level) {
        int sum = 0
        for (int i = level - 1; i > 0; i--) {
            sum += i
            sum += i - 1
        }
        return sum
    }

    static void error(Object msg) {
        logger.error("$TAG: " + msg.toString())
    }
}