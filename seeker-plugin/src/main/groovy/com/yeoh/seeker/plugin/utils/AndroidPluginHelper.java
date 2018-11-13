package com.yeoh.seeker.plugin.utils;

import java.io.File;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.joor.Reflect;

/**
 * Created by Vigi on 2017/3/5.
 */
public class AndroidPluginHelper {

    /**
     * Resolve from com.android.builder.Version#ANDROID_GRADLE_PLUGIN_VERSION
     *
     * Throw exception if can not found
     */
    public static String getAndroidPluginVersion() {
        return Reflect.on("com.android.builder.Version").get("ANDROID_GRADLE_PLUGIN_VERSION");
    }

    /**
     * 执行 compile 命令并且返回生成的目录文件
     */
    public static File resolveBundleDir(Project project, Object variant) {
        // do the trick getting assets task output
        Task mergeAssetsTask = Reflect.on(variant).call("getJavaCompile").get();
        File assetsDir = Reflect.on(mergeAssetsTask).call("getOutputDir").get();
        return assetsDir.getParentFile();
    }
}
