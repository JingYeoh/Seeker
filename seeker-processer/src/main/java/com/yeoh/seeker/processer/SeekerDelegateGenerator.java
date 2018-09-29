package com.yeoh.seeker.processer;

import android.support.annotation.NonNull;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.yeoh.seeker.HideMethod;
import com.yeoh.seeker.HideSeekerDelegate;
import com.yeoh.seeker.annotation.Hide;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @author yangjing .
 * @since 2018-08-21
 */
class SeekerDelegateGenerator {

    private static final String PACKAGE = "com.yeoh.seeker";
    private static final String SEEKER_DELEGATE_IMPL = "HideSeekerDelegateImpl";
    private static final String SEEKER_DELEGATE = HideSeekerDelegate.class.getSimpleName();
    private static final String HIDE_METHOD = HideMethod.class.getSimpleName();

    private String mSubModuleNames[];
    private String mModuleName;
    private RoundEnvironment mRoundEnvironment;
    private Filer mFiler;
    private Map<String, List<HideMethod>> mHideMethodMap;

    SeekerDelegateGenerator(String subModules, String moduleName, Filer filer,
            RoundEnvironment roundEnvironment) {
        mModuleName = moduleName;
        mRoundEnvironment = roundEnvironment;
        mFiler = filer;
        if (subModules != null && subModules.length() > 0) {
            mSubModuleNames = subModules.split(",");
        }
        mHideMethodMap = new HashMap<>();
    }

    boolean generate() throws IOException {
        if (mSubModuleNames == null && mModuleName == null) {
            Log.w("subModuleName and moduleName is null , don't process this module");
            return false;
        }
        generateModuleClass();
        new HideRefBarrierGenerator(mFiler, mHideMethodMap).generate();
        new SeekerDelegateJsonGenerator(mFiler, mHideMethodMap).generate();
        return true;
    }

    // generate module class
    private void generateModuleClass() throws IOException {
        Log.title("========== Generate seeker delegate start ==========");
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
        Log.title("========== Generate seeker delegate end ==========");
    }

    private void appendMethodElement(ExecutableElement element, MethodSpec.Builder moduleConstructor) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        String className = typeElement.getQualifiedName().toString();
        String methodName = element.getSimpleName().toString();
        String returnName = element.getReturnType().toString();
        Hide hide = element.getAnnotation(Hide.class);
        List<String> params = new ArrayList<>();

        Log.second("----- addHideMethod start...");
        Log.i("className = " + className);
        Log.i("methodName = " + methodName);
        Log.i("returnName = " + returnName);

        for (VariableElement it: element.getParameters()) {
            TypeMirror methodParameterType = it.asType();
            String paramClassName = methodParameterType.toString();
            params.add(paramClassName);
        }
        String hideMethodParams = buildHideMethodParams(params);
        moduleConstructor.addStatement("addHideMethod($S,new " + HIDE_METHOD + "($S,$S,$S,$S))", className,
                methodName, returnName, hide.value().toString(), hideMethodParams);
        Log.second("----- addHideMethod done...");
        putHideMethod(className, methodName, returnName, hide.value().toString(), hideMethodParams);
    }

    private String buildHideMethodParams(@NonNull List<String> params) {
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
        Log.i("params = " + builder.toString());
        return builder.toString();
    }

    private String getModuleClassName(String name) {
        if (name == null) {
            return SEEKER_DELEGATE_IMPL;
        }
        return SEEKER_DELEGATE_IMPL + Math.abs(name.trim().hashCode());
    }

    private void putHideMethod(String className, String methodName, String returnName, String modifier, String params) {
        List<HideMethod> hideMethods = mHideMethodMap.get(className);
        if (hideMethods == null) {
            hideMethods = new ArrayList<>();
        }
        HideMethod hideMethod = new HideMethod(methodName, returnName, modifier, params);
        hideMethods.add(hideMethod);
        mHideMethodMap.put(className, hideMethods);
    }
}
