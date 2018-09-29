package com.yeoh.seeker.processer;

/**
 * Used to print log info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
class Log {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static boolean ableDebug = true;

    private static void printWithColor(String info, String color) {
        if (ableDebug) {
            System.out.println(color + info + color);
        }
    }

    public static void print(String info) {
        System.out.println(ANSI_PURPLE + info + ANSI_PURPLE);
    }

    public static void title(String info) {
        printWithColor(info, ANSI_BLUE);
    }

    public static void second(String info) {
        printWithColor(info, ANSI_CYAN);
    }

    public static void i(String info) {
        printWithColor(info, ANSI_WHITE);
    }

    public static void d(String info) {
        printWithColor(info, ANSI_GREEN);
    }

    public static void w(String info) {
        printWithColor(info, ANSI_YELLOW);
    }

    public static void e(String info) {
        printWithColor(info, ANSI_RED);
    }
}
