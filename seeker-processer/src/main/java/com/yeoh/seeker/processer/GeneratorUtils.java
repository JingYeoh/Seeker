package com.yeoh.seeker.processer;

class GeneratorUtils {

    static Class getPrimitiveTypeClass(String className) {
        switch (className) {
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
            case "java.lang.Object":
                return Object.class;
        }
        return null;
    }

    public static Class<?>[] getClasses(String className) {
        if (className.trim().endsWith("[]")) {
            String realClass = className.substring(0, className.length() - 2);
            try {
                Class<?> clazz = Class.forName(realClass);
                return clazz.getClasses();
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    static String getArrayClassName(String className) {
        if (className.trim().endsWith("[]")) {
            return className.substring(0, className.length() - 2);
        }
        return null;
    }

    static boolean isVoid(String className) {
        if (className == null || className.length() == 0) {
            return true;
        }
        if (className.toLowerCase().equals("void")) {
            return true;
        }
        return false;
    }
}
