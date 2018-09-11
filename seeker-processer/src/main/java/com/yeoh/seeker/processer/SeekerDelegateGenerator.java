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

    public SeekerDelegateGenerator(String subModules, String moduleName, Filer filer, RoundEnvironment roundEnvironment) {
        mModuleName = moduleName;
        mRoundEnvironment = roundEnvironment;
        mFiler = filer;
        Log.d("## subModules: " + subModules);
        Log.d("## moduleName: " + moduleName);
    }

    public boolean generate() throws IOException {
        if (mSubModuleNames == null && mModuleName == null) {
            return false;
        }
        generateModuleClass();
        // generate sub module class
        return true;
    }

    // generate module class
    private void generateModuleClass() throws IOException {
        if (mModuleName == null) {
            return;
        }
        TypeSpec.Builder moduleBuilder = buildClass(getModuleClassName(mModuleName));
        moduleBuilder.superclass(ClassName.bestGuess(PACKAGE + "." + SEEKER_DELEGATE));
        MethodSpec.Builder moduleConstructor = MethodSpec.constructorBuilder();
        moduleConstructor.addStatement("super()");

        for (Element it : mRoundEnvironment.getElementsAnnotatedWith(Hide.class)) {
            if (it instanceof ExecutableElement) {
                processMethodElement((ExecutableElement) it, moduleConstructor);
            }
        }
        moduleBuilder.addMethod(moduleConstructor.build());
        JavaFile moduleFile = JavaFile.builder(PACKAGE, moduleBuilder.build())
                .build();
        moduleFile.writeTo(mFiler);
    }

    private void processMethodElement(ExecutableElement element, MethodSpec.Builder moduleConstructor) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        String className = typeElement.getQualifiedName().toString();
        String methodName = element.getSimpleName().toString();
        List<String> params = new ArrayList<>();

        Log.d("## found method " + className + "." + methodName);

        for (VariableElement it : element.getParameters()) {
            TypeMirror methodParameterType = it.asType();
            String paramClassName = methodParameterType.toString();
            params.add(paramClassName);
            Log.d("#### method params: " + paramClassName);
        }

        moduleConstructor.addStatement("addHideMethod($S, "
                        + buildHideMethod(methodName, params)
                        + ")",
                className);
    }

    private TypeSpec.Builder buildClass(String className) {
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
    }

    private String buildHideMethod(@NonNull String methodName, @NonNull List<String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("new")
                .append(" ")
                .append(HIDE_METHOD)
                .append("(")
                .append("\"")
                .append(methodName)
                .append("\"");
        for (int i = 0; i < params.size(); i++) {
            builder.append(", ");
            builder.append("\"");
            builder.append(params.get(i));
            builder.append("\"");
        }
        builder.append(")");

        Log.d(builder.toString());
        return builder.toString();
    }

    private String getModuleClassName(String name) {
        return SEEKER_DELEGATE_IMPL + Math.abs(name.hashCode());
    }
}
