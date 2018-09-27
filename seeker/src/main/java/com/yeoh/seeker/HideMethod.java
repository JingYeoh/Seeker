package com.yeoh.seeker;

import com.yeoh.seeker.annotation.Modifier;

/**
 * Used to save method info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class HideMethod {

    public final String methodName;
    public final String[] params;
    private final String modifier;
    private final String returnValue;

    public HideMethod(String methodName, String returnValue, String modifier, String params) {
        this.methodName = methodName;
        this.modifier = modifier;
        this.returnValue = returnValue;
        if (params != null && params.length() > 0) {
            this.params = params.split(",");
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

    public String generateCode() {
        return generateCodeStr(HideMethod.class.getSimpleName());
    }


    public String generateCodeStr(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ")
                .append(name)
                .append("(")
                .append("\"")
                .append(methodName)
                .append("\"")
                .append(",")
                .append("\"")
                .append(returnValue)
                .append("\"")
                .append(",")
                .append("\"")
                .append(modifier)
                .append("\"")
                .append(",");
        if (params != null && params.length > 0) {
            builder.append("\"");
            for (int i = 0; i < params.length; i++) {
                String arg = params[i];
                if (i != 0) {
                    builder.append(",");
                }
                builder.append(arg);
            }
            builder.append("\"");
        } else {
            builder.append("null");
        }
        builder.append(")");
        return builder.toString();
    }

    public String generateCodeWithJavaPoet() {
        return generateCodeStr("$T");
    }
}
