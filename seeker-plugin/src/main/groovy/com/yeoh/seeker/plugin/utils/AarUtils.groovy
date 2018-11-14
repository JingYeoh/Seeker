package com.yeoh.seeker.plugin.utils
/**
 * aar 的帮助类，用于解压、重新压缩等
 */
class AarUtils {

    /**
     * 解压 aar
     * @param path aar 路径
     * @param unAarDir 解压的目录
     */
    static Aar unAar(String path, File unAarDir) {
        return unAar(new File(path), unAarDir)
    }

    /**
     * 解压 aar
     * @param inputStream aar 的输入流
     * @param unAarDir 解压的目录
     */
    static Aar unAar(File file, File unAarDir) {
        if (!unAarDir.exists()) {
            unAarDir.mkdirs()
        }
        Aar aar = new Aar()
        aar.rootPath = file.path
        //1. 复制文件并重命名 .aar 后缀变为 .zip 后缀
        String aarName = file.name.replace(".aar", '')
        String copyPath = "${unAarDir.path}/${aarName}.zip"
        def copy = "cp ${file.path} ${copyPath}"
        copy.execute()
        //2. 解压
        File unAar = new File("${copyPath}")

        //3. 取出相应的信息

        return aar
    }

    /**
     * 存储 aar 解压后的信息
     */
    static class Aar {
        String rootPath
        String jarPath
    }
}