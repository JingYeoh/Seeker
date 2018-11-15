package com.yeoh.seeker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used to process reflect method  .
 *
 * @author yangjing .
 * @since 2018-09-20
 */
public class HideRefDelegate {

    private final Object object;
    private final Class mClass;

    public HideRefDelegate(@NonNull Object object) {
        this.object = object;
        mClass = object.getClass();
    }

    protected Object invokeMethod(Method method, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Object invokeMethodForVarArgs(Method method, Object arg) {
        try {
            return method.invoke(object, (Object) arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Method reflectMethod(HideMethod hideMethod) {
        Method method = HideRefFactory.getMethod(mClass, hideMethod);
        try {
            if (method == null) {
                method = mClass.getDeclaredMethod(hideMethod.methodName, getParams(hideMethod));
                method.setAccessible(true);
                HideRefFactory.putMethod(mClass, hideMethod, method);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    @Nullable
    private Class[] getParams(HideMethod hideMethod) throws ClassNotFoundException {
        Class<?>[] params = null;
        if (hideMethod.params != null && hideMethod.params.length > 0) {
            params = new Class[hideMethod.params.length];
            for (int i = 0; i < hideMethod.params.length; i++) {
                params[i] = guessClassForName(hideMethod.params[i]);
            }
        }
        return params;
    }

    /**
     * 根据类名猜测 Class
     *
     * @param className 类名
     *
     * @return Class or null
     */
    @Nullable
    private static Class guessClassForName(String className) throws ClassNotFoundException {
        if (className == null || className.trim().isEmpty()) {
            return null;
        }
        switch (className.toLowerCase()) {
            case "void":
                return void.class;
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "char":
                return char.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "java.lang.object":
                return Object.class;
        }
        // 如果是数组
        if (className.endsWith("[]")) {
            String realClassName = className.substring(0, className.length() - 2);
            return getArrayTypeClass(realClassName);
        }
        return Class.forName(className);
    }

    /**
     * 返回数组类型的 Class
     */
    private static Class getArrayTypeClass(String className) throws ClassNotFoundException {
        if (className == null || className.trim().isEmpty()) {
            return null;
        }
        switch (className.toLowerCase()) {
            case "void":
                return void.class;
            case "boolean":
                return boolean[].class;
            case "byte":
                return byte[].class;
            case "short":
                return short[].class;
            case "int":
                return int[].class;
            case "long":
                return long[].class;
            case "char":
                return char[].class;
            case "float":
                return float[].class;
            case "double":
                return double[].class;
            case "java.lang.object":
                return Object[].class;
        }
        return Class.forName("[L" + className + ";");
    }
}
