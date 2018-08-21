package com.yeoh.seeker.processer;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Used to generate SeekerDelegate class code .
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
class SeekerDelegateGenerator {

    private static final String SEEKER_DELEGATE_CLASS = "SeekerDelegate";
    private static final String SEEKER_DELEGATE_PACKAGE = "com.yeoh.seeker";

    final private Map<String, List<HideMethod>> mAnnotatedHideMap;
    final private Filer mFiler;

    SeekerDelegateGenerator(Map<String, List<HideMethod>> annotatedHideMap, Filer filer) {
        mAnnotatedHideMap = annotatedHideMap;
        mFiler = filer;
    }

    boolean generate() {
        if (mAnnotatedHideMap == null || mAnnotatedHideMap.isEmpty()) {
            Log.d("@Hide is not found in project!!!");
            return false;
        }
        Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        for (String className : mAnnotatedHideMap.keySet()) {
            List<HideMethod> methods = mAnnotatedHideMap.get(className);
            for (HideMethod method : methods) {

            }
        }

        return true;
    }
}
