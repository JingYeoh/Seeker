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

    static void d_(Object msg) {
        if (Debug) {
            print("$TAG: " + msg.toString())
        }
    }

    static void _d(Object msg) {
        if (Debug) {
            println("$TAG: " + msg.toString())
        }
    }

    static void error(Object msg) {
        logger.error("$TAG: " + msg.toString())
    }
}