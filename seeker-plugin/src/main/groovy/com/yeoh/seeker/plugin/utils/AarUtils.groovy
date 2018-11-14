package com.yeoh.seeker.plugin.utils
/**
 * aar 的帮助类，用于解压、重新压缩等
 */
class AarUtils {

    private static final int LEVEL = 6
    private static final String GROUP = "AarUtils"

    /**
     * 返回推荐的 aar　解压目录
     *
     * @param path aar　文件路径
     * @return 解压的 aar　路径
     */
    static String getExtractAarPath(String path) {
        return getExtractAarPath(new File(path))
    }

    /**
     * 返回推荐的 aar　解压目录
     *
     * @param aarFile aar　文件
     * @return 解压的 aar　路径
     */
    static String getExtractAarPath(File aarFile) {
        return aarFile.getParent() + "/" + aarFile.getName().replace(".aar", "")
    }

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
        String copy = "cp ${file.path} ${copyPath}"
        copy.execute()
        Log.i(LEVEL, GROUP, "execute ${copy}")
        //2. 解压
        File zipAar = new File("${copyPath}")
        String unZip = "unzip -o ${zipAar.path} -d ${zipAar.getParent()}"
        unZip.execute().waitFor()
        Log.i(LEVEL, GROUP, "execute ${unZip}")
        //3. 取出相应的信息
        aar.jarPath = "${zipAar.getParent()}/classes.jar"
        //4. 解压 jar　包
        String extractJarPath = JarUtils.getExtractJarPath(aar.jarPath)
        JarUtils.unJar(new File(aar.jarPath), new File(extractJarPath))
        aar.extractJarPath = extractJarPath
        return aar
    }

    /**
     * 存储 aar 解压后的信息
     */
    static class Aar {
        String rootPath // .aar 根目录
        String jarPath // .jar　所在的路径
        String extractJarPath //解压后的 jar　路径
    }
}