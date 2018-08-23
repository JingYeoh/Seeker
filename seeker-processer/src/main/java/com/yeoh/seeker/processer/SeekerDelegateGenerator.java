package com.yeoh.seeker.processer;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Filer;

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
        ClassName delegateName = ClassName.get(SEEKER_DELEGATE_CLASS, SEEKER_DELEGATE_PACKAGE);
        FieldSpec instanceField = generateInstanceField(delegateName);
        Builder constructorBuilder = generateConstructor();

        TypeSpec.classBuilder(delegateName);
        for (String className : mAnnotatedHideMap.keySet()) {
            List<HideMethod> methods = mAnnotatedHideMap.get(className);
            for (HideMethod method : methods) {
                constructorBuilder.addStatement("");
            }
        }
        MethodSpec constructor = constructorBuilder.build();


        return true;
    }

    private FieldSpec generateInstanceField(ClassName delegateName) {
        return FieldSpec.builder(String.class, "sInstance")
                .addModifiers(PRIVATE, STATIC, FINAL)
                .initializer("new $T", delegateName)
                .build();
    }

    private FieldSpec generateHideMap() {
        ClassName map = ClassName.get("java.util", "Map");
        return FieldSpec.builder(String.class, "mHideMap")
                .addModifiers(PRIVATE)
                .initializer("new $T", HashMap.class)
                .build();
    }

    private MethodSpec.Builder generateConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(PRIVATE);
    }
}
