package com.yeoh.seeker.plugin.utils

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * zip 的帮助类，用于解压、重新压缩等
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/15
 */
class ZipUtils {

    private static final int LEVEL = 6
    private static final String GROUP = "ZipUtils"

    /**
     * 解压 Zip 文件
     * @param zipPath zip 文件目录
     * @param unZipDir 解压的文件夹
     */
    static void unZip(String zipPath, File unZipDir) {
        unZip(new File(zipPath), unZipDir)
    }
    /**
     * 解压 Zip 文件
     * @param zipFile zip 文件
     * @param unZipDir 解压的文件夹
     */
    static void unZip(File zipFile, File unZipDir) {
//        "unzip -o ${zipFile.path} -d ${unZipDir.getParent()}".execute()

        if (!unZipDir.exists()) {
            unZipDir.mkdirs()
        }
        def zip = new ZipFile(zipFile)
        zip.entries().each {
            if (!it.isDirectory()) {
                def fOut = new File(unZipDir.path + File.separator + it.name)
                //create output dir if not exists
                new File(fOut.parent).mkdirs()
                def fos = new FileOutputStream(fOut)
                //println "name:${it.name}, size:${it.size}"
                def buf = new byte[it.size]
                def len = zip.getInputStream(it).read(buf) //println zip.getInputStream(it).text
                fos.write(buf, 0, len)
                fos.close()
            }
        }
        zip.close()
    }

    /**
     * 压缩 Zip 文件
     * @param unZipFile 要压缩的文件
     * @param zipDir 要压缩到的文件夹
     */
    static void zip(File unZipFile, File zipDir) {
        ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(unZipFile))
        zipDir.eachFile() { file ->
            //check if file
            if (file.isFile()) {
                zipFile.putNextEntry(new ZipEntry(file.name))
                def buffer = new byte[file.size()]
                file.withInputStream {
                    zipFile.write(buffer, 0, it.read(buffer))
                }
                zipFile.closeEntry()
            }
        }
        zipFile.close()
    }
}