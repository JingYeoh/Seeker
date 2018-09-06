package com.yeoh.seeker;

/**
 * Used to save method info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class HideMethod {

    private final String methodName;
    private final String[] params;

    public HideMethod(String methodName, String... params) {
        this.methodName = methodName;
        this.params = params;
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
