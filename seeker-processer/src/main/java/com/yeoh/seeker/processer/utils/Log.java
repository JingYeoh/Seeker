package com.yeoh.seeker.processer.utils;

/**
 * Used to print log info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class Log {

    private static boolean ableDebug = false;

    public static void print(String info) {
        System.out.println(info);
    }

    public static void d(String info) {
        if (ableDebug) {
            print(info);
        }
    }
}
