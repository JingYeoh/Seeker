package com.yeoh.seeker.processer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;
import com.yeoh.seeker.HideMethod;
import com.yeoh.seeker.HideRefBarrier;
import com.yeoh.seeker.processer.utils.GeneratorUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Used to generate HideRecBarrier class code .
 *
 * @author yangjing .
 * @since 2018-09-21
 */
class HideRefBarrierGenerator {

    private static final String PACKAGE = "com.yeoh.seeker";
    private static final String SUFFIX = "$$RefBarrier";
    private static final String NAME_ARG = "arg";
    private static final String HIDE_REF_BARRIER = HideRefBarrier.class.getSimpleName();
    private Map<String, List<HideMethod>> mHideMethodMap;
    private Filer mFiler;

    HideRefBarrierGenerator(Filer filer, Map<String, List<HideMethod>> hideMethodMap) {
        mFiler = filer;
        mHideMethodMap = hideMethodMap;
    }

    void generate() throws IOException {
        if (mHideMethodMap == null || mHideMethodMap.isEmpty()) {
            return;
        }
        for (String className: mHideMethodMap.keySet()) {
            generateHideRefBarrier(className, mHideMethodMap.get(className));
        }
    }

    private void generateHideRefBarrier(String classFullName, List<HideMethod> hideMethods) throws IOException {
        String[] splitStr = classFullName.split("\\.");
        String className = splitStr[splitStr.length - 1];
        String packageName = classFullName.substring(0, classFullName.length() - className.length() - 1);

        TypeSpec.Builder moduleBuilder = TypeSpec.classBuilder(className + SUFFIX)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        moduleBuilder.superclass(ClassName.bestGuess(PACKAGE + "." + HIDE_REF_BARRIER));
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, "object")
                .addStatement("super(object)")
                .build();
        moduleBuilder.addMethod(constructor);
        for (HideMethod hideMethod: hideMethods) {
            Builder methodBuilder = MethodSpec.methodBuilder(hideMethod.methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            appendMethodParams(methodBuilder, hideMethod.params);
            String methodArgs = buildArgs(hideMethod.params);
            if (methodArgs == null) {
                methodBuilder.addStatement("invokeMethod("
                        + buildInvokeMethod(hideMethod)
                        + ")", HideMethod.class);
            } else {
                methodBuilder.addStatement("invokeMethod("
                        + buildInvokeMethod(hideMethod)
                        + ", $N)", HideMethod.class, methodArgs);
            }
            moduleBuilder.addMethod(methodBuilder.build());
        }

        JavaFile moduleFile = JavaFile.builder(packageName, moduleBuilder.build())
                .build();
        moduleFile.writeTo(mFiler);
    }

    private String buildInvokeMethod(HideMethod hideMethod) {
        return "reflectMethod(" + hideMethod.generateCodeWithJavaPoet() + ")";
    }

    private String buildArgs(String[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            builder.append(NAME_ARG)
                    .append(i);
            if (i != args.length - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private void appendMethodParams(Builder methodBuilder, String[] params) {
        if (params == null || params.length == 0) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            String arg = params[i];
            String argName = NAME_ARG + i;
            try {
                Class argClass = GeneratorUtils.getPrimitiveTypeClass(arg);
                if (argClass == null) {
                    methodBuilder.addParameter(Class.forName(arg), argName);
                } else {
                    methodBuilder.addParameter(argClass, argName);
                }
            } catch (ClassNotFoundException e) {
                methodBuilder.addParameter(ClassName.bestGuess(arg), argName);
            }
        }
    }
}
