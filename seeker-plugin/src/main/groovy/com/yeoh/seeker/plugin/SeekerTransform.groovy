package com.yeoh.seeker.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yeoh.seeker.plugin.utils.Log
import com.yeoh.seeker.plugin.utils.ThrowExecutionError
import groovy.json.JsonSlurper
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class SeekerTransform extends Transform {

    static final int LOG_LEVEL = 1
    static final String GROUP = "SeekerTransform"
    private Project mProject
    static ClassPool pool
    static List<DirectoryInput> classFileList
    static List<String> jarPathList
    static List<CtClass> jarClassList
    static final String PATH_SEEKER_JSON = "./build/Seeker/seeker.json"

    SeekerTransform(Project project) {
        mProject = project
        pool = ClassPool.getDefault()
    }

    @Override
    String getName() {
        return "Seeker"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Collection<TransformInput> inputs = transformInvocation.getInputs()
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        Log.i(LOG_LEVEL, GROUP, '----------------- SEEKER START -----------------')
        readSeekerConfig(inputs)
        pool.appendClassPath(mProject.android.bootClasspath[0].toString())

        jarClassList = new ArrayList<>()
        classFileList = new ArrayList<>()
        jarPathList = new ArrayList<>()

        inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                pool.appendClassPath(jarInput.file.absolutePath)
                copyJar(jarInput, outputProvider)
            }

            input.directoryInputs.each { DirectoryInput directoryInput ->
                pool.appendClassPath(directoryInput.file.absolutePath)
                classFileList.add(directoryInput)
            }
        }

        processJar()
    }

    /**
     * 从 json 文件中读取 seeker 的配置
     */
    private static void readSeekerConfig(Collection<TransformInput> inputs) {
        DataSource.clear()
        Log.i(LOG_LEVEL + 1, GROUP, "readSeekerConfig")
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File configFile = new File(PATH_SEEKER_JSON)
                Log.i(LOG_LEVEL + 2, GROUP, configFile.path)
                if (configFile.exists()) {
                    def content = new StringBuilder()
                    configFile.eachLine("UTF-8") {
                        content.append(it)
                    }
                    Map data = new JsonSlurper().parseText(content.toString())
                    data.keySet().forEach {
                        DataSource.seekerConfig.put(it, data.get(it))
                    }
                } else {
                    ThrowExecutionError.throwError("seeker.json does not exist")
                }
            }
        }
    }

    /**
     * 复制 jar 包
     */
    private static void copyJar(JarInput jarInput, TransformOutputProvider outputProvider) {
        def jarName = jarInput.name
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4)
        }
        def dest = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        Log.i(LOG_LEVEL + 1, GROUP, "copyJar:: dest path = " + dest.path)
        FileUtils.copyFile(jarInput.file, dest)
        jarPathList.add(dest.getAbsolutePath())
    }

    /**
     * 处理 jar 包
     */
    private static void processJar() {
        for (String jarPath : jarPathList) {
            JarInject.injectJar(jarPath)
        }
    }
}