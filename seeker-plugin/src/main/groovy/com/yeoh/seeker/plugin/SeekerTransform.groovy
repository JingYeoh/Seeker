package com.yeoh.seeker.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yeoh.seeker.plugin.utils.Log
import groovy.json.JsonSlurper
import javassist.ClassPool
import org.gradle.api.Project

class SeekerTransform extends Transform {

    private Project mProject
    private ClassPool mClassPool

    SeekerTransform(Project project) {
        mProject = project
        mClassPool = ClassPool.getDefault()
    }

    @Override
    String getName() {
        return "Seeker"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
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
        super.transform(transformInvocation)
        Log.d('------------- Seeker start -----------------')
        readSeekerConfig(transformInvocation.inputs)
        transformInvocation.inputs.forEach({
            it.jarInputs.forEach({
                Log.d(it.file.absolutePath)
                Log.d(it.name)
            })
        })
    }

    /**
     * read seeker config from json file
     */
    private static void readSeekerConfig(Collection<TransformInput> inputs) {
        DataSource.clear()
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File configFile = new File(directoryInput.file.absolutePath + "com/yeoh/seeker/seeker.json")
                Log.d(configFile.path)
                if (configFile.exists()) {
                    def content = new StringBuilder()
                    configFile.eachLine("UTF-8") {
                        content.append(it)
                    }
                    def data = new JsonSlurper().parseText(content.toString())

                }
            }
        }
    }
}