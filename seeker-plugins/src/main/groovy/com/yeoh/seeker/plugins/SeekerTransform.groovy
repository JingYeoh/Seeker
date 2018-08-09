package com.yeoh.seeker.plugins

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yeoh.seeker.plugins.utils.Log
import org.gradle.api.Project

class SeekerTransform extends Transform {

    private Project project

    SeekerTransform(Project project) {
        this.project = project
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

        project.task('seeker') {
            doLast {
                Log.d("Hello from SeekerPlugins")
            }
        }
    }
}