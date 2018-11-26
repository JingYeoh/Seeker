package com.yeoh.seeker;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to save reflect object .
 *
 * @author yangjing .
 * @since 2018-09-15
 */
public class HideRefFactory {

    private static Map<Class, Map<String, Method>> methodMap;

    static Method getMethod(Class refName, HideMethod hideMethod) {
        if (methodMap == null) {
            return null;
        }
        Map<String, Method> methods = methodMap.get(refName);
        if (methods == null) {
            return null;
        }
        return methods.get(hideMethod.toString());
    }

    static void putMethod(Class refName, HideMethod hideMethod, Method method) {
        if (methodMap == null) {
            methodMap = new HashMap<>();
        }
        Map<String, Method> methods = methodMap.get(refName);
        if (methods == null) {
            methods = new HashMap<>();
            methodMap.put(refName, methods);
        }
        methods.put(hideMethod.toString(), method);
    }
}
