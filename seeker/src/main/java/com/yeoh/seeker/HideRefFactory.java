package com.yeoh.seeker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to save reflect object .
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-09-15
 */
public class HideRefFactory {

    private static Map<Class, Map<String, Method>> methodMap;

    protected static Method getMethod(Class refName, String name) {
        if (methodMap == null) {
            return null;
        }
        Map<String, Method> methods = methodMap.get(refName);
        if (methods == null) {
            return null;
        }
        return methods.get(name);
    }

    protected static void putMethod(Class refName, String name, Method method) {
        if (methodMap == null) {
            methodMap = new HashMap<>();
        }
        Map<String, Method> methods = methodMap.get(refName);
        if (methods == null) {
            methods = new HashMap<>();
            methodMap.put(refName, methods);
        }
        methods.put(name, method);
    }
}
