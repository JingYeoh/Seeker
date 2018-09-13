package com.yeoh.seeker;

import com.yeoh.seeker.annotation.Modifier;

/**
 * Used to save method info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class HideMethod {

    private final String methodName;
    private final String[] params;
    private String modifier;

    public HideMethod(String methodName, String modifier, String params) {
        this.methodName = methodName;
        this.modifier = modifier;
        if (params != null && params.length() > 0) {
            this.params = params.split(params);
        } else {
            this.params = null;
        }
    }

    public Modifier getModifier() {
        if (modifier.equals(Modifier.DEFAULT.toString())) {
            return Modifier.DEFAULT;
        } else if (modifier.equals(Modifier.PRIVATE.toString())) {
            return Modifier.PRIVATE;
        } else if (modifier.equals(Modifier.PUBLIC.toString())) {
            return Modifier.PUBLIC;
        } else if (modifier.equals(Modifier.PROTECTED.toString())) {
            return Modifier.PROTECTED;
        }
        return Modifier.DEFAULT;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HideMethod)) {
            return false;
        }
        HideMethod target = (HideMethod) obj;
        if (target.methodName == null || !methodName.equals(target.methodName)) {
            return false;
        }
        if (params != null) {
            if (target.params == null) {
                return false;
            }
            if (params.length != target.params.length) {
                return false;
            }
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals(target.params[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
