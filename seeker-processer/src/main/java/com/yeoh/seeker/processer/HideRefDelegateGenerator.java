package com.yeoh.seeker.processer;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;
import com.yeoh.seeker.HideMethod;
import com.yeoh.seeker.HideRefDelegate;
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
class HideRefDelegateGenerator {

    private static final String PACKAGE = "com.yeoh.seeker";
    private static final String SUFFIX = "RefDelegate";
    private static final String PREFIX = "_";
    private static final String NAME_ARG = "arg";
    private static final String HIDE_REF_DELEGATE = HideRefDelegate.class.getSimpleName();
    private Map<String, List<HideMethod>> mHideMethodMap;
    private Filer mFiler;

    HideRefDelegateGenerator(Filer filer, Map<String, List<HideMethod>> hideMethodMap) {
        mFiler = filer;
        mHideMethodMap = hideMethodMap;
    }

    void generate() throws IOException {
        if (mHideMethodMap == null || mHideMethodMap.isEmpty()) {
            Log.w("hideMethodMap is empty...");
            return;
        }
        Log.title("========== Generate HideRefDelegate start ==========");
        for (String className: mHideMethodMap.keySet()) {
            generateHideRefBarrier(className, mHideMethodMap.get(className));
        }
        Log.title("========== Generate HideRefDelegate end ==========");
    }

    private void generateHideRefBarrier(String classFullName, List<HideMethod> hideMethods) throws IOException {
        String[] splitStr = classFullName.split("\\.");
        String className = splitStr[splitStr.length - 1];
        String packageName = classFullName.substring(0, classFullName.length() - className.length() - 1);

        Log.second("--------- generate class " + generateRefBarrierClassName(className) + " start ...");

        TypeSpec.Builder moduleBuilder = TypeSpec.classBuilder(generateRefBarrierClassName(className))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        moduleBuilder.superclass(ClassName.bestGuess(PACKAGE + "." + HIDE_REF_DELEGATE));
        // 添加构造方法
        Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        appendMethodParams(constructorBuilder, classFullName);
        constructorBuilder.addStatement("super(" + NAME_ARG + "0)");
        moduleBuilder.addMethod(constructorBuilder.build());
        // 添加方法体
        for (HideMethod hideMethod: hideMethods) {
            Log.second("----- start to generate method");
            Log.i(hideMethod.toString());

            Builder methodBuilder = MethodSpec.methodBuilder(hideMethod.methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            appendMethodParams(methodBuilder, hideMethod.params);
            appendMethodReturns(methodBuilder, hideMethod.returns);

            String methodArgs = buildArgs(hideMethod.params);
            if (methodArgs == null) {
                if (GeneratorUtils.isVoid(hideMethod.returns)) {
                    methodBuilder.addStatement("invokeMethod("
                            + buildInvokeMethod(hideMethod) + ")", HideMethod.class);
                } else {
                    methodBuilder.addStatement("return" + " (" + hideMethod.returns + ") "
                                    + "invokeMethod(" + buildInvokeMethod(hideMethod) + ")",
                            HideMethod.class);
                }
            } else {
                if (GeneratorUtils.isVoid(hideMethod.returns)) {
                    // 如果只有一个参数并且该参数为可变长度，则需要特殊处理
                    if (hideMethod.params.length > 1 || !hideMethod.params[0].endsWith("[]")) {
                        methodBuilder.addStatement("invokeMethod("
                                + buildInvokeMethod(hideMethod) + ", $N)", HideMethod.class, methodArgs);
                    } else {
                        methodBuilder.addStatement("invokeMethodForVarArgs("
                                + buildInvokeMethod(hideMethod) + ", $N)", HideMethod.class, methodArgs);
                    }
                } else {
                    if (hideMethod.params.length > 1 || !hideMethod.params[0].endsWith("[]")) {
                        methodBuilder.addStatement("return" + " (" + hideMethod.returns + ") "
                                        + "invokeMethod(" + buildInvokeMethod(hideMethod) + ", $N)",
                                HideMethod.class, methodArgs);
                    } else {
                        methodBuilder.addStatement("return" + " (" + hideMethod.returns + ") "
                                        + "invokeMethodForVarArgs(" + buildInvokeMethod(hideMethod) + ", $N)",
                                HideMethod.class, methodArgs);
                    }
                }
            }
            moduleBuilder.addMethod(methodBuilder.build());
        }

        JavaFile moduleFile = JavaFile.builder(packageName, moduleBuilder.build())
                .build();
        moduleFile.writeTo(mFiler);
        Log.second("--------- generate class " + className + SUFFIX + " end ...");
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

    /**
     * 给方法添加参数
     *
     * @param methodBuilder 生成方法的 Builder
     * @param params        方法的的参数
     */
    private void appendMethodParams(Builder methodBuilder, String... params) {
        Log.second("----- append method params start ...");
        if (params == null || params.length == 0) {
            return;
        }
        for (int i = 0; i < params.length; i++) {
            String arg = params[i];
            String argName = NAME_ARG + i;
            String arrayClassName = GeneratorUtils.getArrayClassName(arg);
            if (arrayClassName != null) {
                try {
                    methodBuilder.addParameter(ArrayTypeName.of(Class.forName(arrayClassName)), argName);
                } catch (ClassNotFoundException e) {
                    methodBuilder.addParameter(ArrayTypeName.of(ClassName.bestGuess(arrayClassName)), argName);
                }
            } else {
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
        Log.second("----- append method params end ...");
    }

    /**
     * 给方法添加返回值
     *
     * @param methodBuilder 方法 Builder
     * @param returns       返回值类型的字符串
     */
    private void appendMethodReturns(Builder methodBuilder, String returns) {
        Log.second("----- append method returns start ...");
        if (returns == null || returns.length() == 0) {
            return;
        }
        if (GeneratorUtils.isVoid(returns)) {
            return;
        }
        String arrayClassName = GeneratorUtils.getArrayClassName(returns);
        if (arrayClassName != null) {
            try {
                methodBuilder.returns(ArrayTypeName.of(Class.forName(arrayClassName)));
            } catch (ClassNotFoundException e) {
                methodBuilder.returns(ArrayTypeName.of(ClassName.bestGuess(arrayClassName)));
            }
        } else {
            Class returnClass = GeneratorUtils.getPrimitiveTypeClass(returns);
            if (returnClass == null) {
                try {
                    methodBuilder.returns(Class.forName(returns));
                } catch (ClassNotFoundException e) {
                    methodBuilder.returns(ClassName.bestGuess(returns));
                }
            } else {
                methodBuilder.returns(returnClass);
            }
        }
        Log.second("----- append method returns end ...");
    }

    /**
     * 生成反射代理类的类名
     */
    private String generateRefBarrierClassName(String className) {
        return PREFIX + className + SUFFIX;
    }
}
