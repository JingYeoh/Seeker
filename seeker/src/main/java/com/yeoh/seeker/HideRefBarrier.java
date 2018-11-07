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
public class HideRefBarrier {

    protected final Object object;
    protected final Class mClass;

    public HideRefBarrier(@NonNull Object object) {
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
                params[i] = Class.forName(hideMethod.params[i]);
            }
        }
        return params;
    }
}
