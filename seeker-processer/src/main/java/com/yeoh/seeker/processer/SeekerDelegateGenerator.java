package com.yeoh.seeker.processer;

import android.support.annotation.NonNull;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yeoh.seeker.annotation.Hide;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Used to generate SeekerDelegate class code .
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
class SeekerDelegateGenerator {

    private static final String PACKAGE = "com.yeoh.seeker";
    private static final String SEEKER_DELEGATE_IMPL = "HideSeekerDelegateImpl";
    private static final String SEEKER_DELEGATE = "HideSeekerDelegate";
    private static final String HIDE_METHOD = "HideMethod";

    private String mSubModuleNames[];
    private String mModuleName;
    private RoundEnvironment mRoundEnvironment;
    private Filer mFiler;

    public SeekerDelegateGenerator(String subModules, String moduleName, Filer filer,
            RoundEnvironment roundEnvironment) {
        mModuleName = moduleName;
        mRoundEnvironment = roundEnvironment;
        mFiler = filer;
        if (subModules != null && subModules.length() > 0) {
            mSubModuleNames = subModules.split(",");
        }
    }

    public boolean generate() throws IOException {
        if (mSubModuleNames == null && mModuleName == null) {
            return false;
        }
        generateModuleClass();
        return true;
    }

    // generate module class
    private void generateModuleClass() throws IOException {
        TypeSpec.Builder moduleBuilder = TypeSpec.classBuilder(getModuleClassName(mModuleName))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        moduleBuilder.superclass(ClassName.bestGuess(PACKAGE + "." + SEEKER_DELEGATE));
        MethodSpec.Builder moduleConstructor = MethodSpec.constructorBuilder();
        moduleConstructor.addStatement("super()");

        for (Element it: mRoundEnvironment.getElementsAnnotatedWith(Hide.class)) {
            if (it instanceof ExecutableElement) {
                appendMethodElement((ExecutableElement) it, moduleConstructor);
            }
        }
        if (mSubModuleNames != null && mSubModuleNames.length > 0) {
            for (String name: mSubModuleNames) {
                moduleConstructor.addStatement("mHideMethods.putAll(new "
                        + getModuleClassName(name)
                        + "().getHideMethods()"
                        + ")"
                );
            }
        }
        moduleBuilder.addMethod(moduleConstructor.build());
        JavaFile moduleFile = JavaFile.builder(PACKAGE, moduleBuilder.build())
                .build();
        moduleFile.writeTo(mFiler);
    }

    private void appendMethodElement(ExecutableElement element, MethodSpec.Builder moduleConstructor) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        String className = typeElement.getQualifiedName().toString();
        String methodName = element.getSimpleName().toString();
        Hide hide = element.getAnnotation(Hide.class);
        List<String> params = new ArrayList<>();

        for (VariableElement it: element.getParameters()) {
            TypeMirror methodParameterType = it.asType();
            String paramClassName = methodParameterType.toString();
            params.add(paramClassName);
        }
        moduleConstructor.addStatement("addHideMethod($S,new " + HIDE_METHOD + "($S,$S,$S))", className,
                methodName, hide.value().toString(), buildHideMethod(params));
    }

    private String buildHideMethod(@NonNull List<String> params) {
        if (params.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(params.get(i));
        }
        Log.d(builder.toString());
        return builder.toString();
    }

    private String getModuleClassName(String name) {
        if (name == null) {
            return SEEKER_DELEGATE_IMPL;
        }
        return SEEKER_DELEGATE_IMPL + Math.abs(name.trim().hashCode());
    }
}
