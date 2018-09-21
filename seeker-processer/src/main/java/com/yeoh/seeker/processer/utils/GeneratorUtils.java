package com.yeoh.seeker.processer.utils;

public class GeneratorUtils {

    public static Class getPrimitiveTypeClass(String className) {
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
}
