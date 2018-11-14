package com.yeoh.seeker.plugin.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 解析 aar　中的资源
 *
 * @author Yeoh @ Zhihu Inc.
 * @since 2018/11/14
 */
public class AndroidArchiveLibrary {

    private final String mPath;

    public AndroidArchiveLibrary(String path) {
        mPath = path;
        if (!path.endsWith("aar")) {
            throw new IllegalArgumentException("artifact must be aar type!");
        }
    }

    /**
     * 获取 jar 所在的根目录
     *
     * @return 目录文件
     */
    private File getJarsRootFolder() {
        return new File(mPath, "jars");
    }

    /**
     * 获取 aar　中的 class.jar　文件
     *
     * @return jar 文件
     */
    public File getClassesJarFile() {
        return new File(getJarsRootFolder(), "classes.jar");
    }

    /**
     * 获取本地依赖的 jar 包
     *
     * @return 文件集合
     */
    public Collection<File> getLocalJars() {
        List<File> localJars = new ArrayList<>();
        File[] jarList = new File(getJarsRootFolder(), "libs").listFiles();
        if (jarList != null) {
            for (File jars: jarList) {
                if (jars.isFile() && jars.getName().endsWith(".jar")) {
                    localJars.add(jars);
                }
            }
        }
        return localJars;
    }
}