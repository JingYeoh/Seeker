package com.yeoh.seeker.plugins

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yeoh.seeker.plugins.utils.Log
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

        mProject.task('seeker') {
            doLast {
                Log.d("Hello from SeekerPlugins")
            }
        }

        Log.d('------------- Seeker start -----------------')

        transformInvocation.inputs.forEach({
            it.jarInputs.forEach({
                Log.d(it.file.absolutePath)
                Log.d(it.name)
            })
        })
    }
}