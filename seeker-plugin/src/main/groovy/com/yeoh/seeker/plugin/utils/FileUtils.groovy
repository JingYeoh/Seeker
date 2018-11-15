package com.yeoh.seeker.plugin.utils
/**
 * 文件的帮助类，用于复制、重命名等
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/15
 */
class FileUtils {

    private static final int LEVEL = 6
    private static final String GROUP = "FileUtils"

    /**
     * 复制文件
     * @param from 要复制的文件
     * @param toDir 要移动的目标文件夹
     */
    static void copy(File from, File toDir) {
        copy(from, toDir, from.name)
    }

    /**
     * 复制文件并重命名
     * @param from 要复制的文件
     * @param toDir 要移动的目标文件夹
     * @param reName 重命名的名字
     */
    static void copy(File from, File toDir, String reName) {
        String targetFilePath = toDir.path
        targetFilePath = !targetFilePath.endsWith("/") ? "${targetFilePath}/" : targetFilePath
        File targetFile = new File("${targetFilePath}${reName}")
        targetFile.write(from.text)
    }
}