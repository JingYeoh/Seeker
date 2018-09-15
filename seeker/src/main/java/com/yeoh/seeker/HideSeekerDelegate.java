package com.yeoh.seeker;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to save object annotated with {@link com.yeoh.seeker.annotation.Hide} .
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-09-06
 */
public class HideSeekerDelegate {

    protected Map<String, List<HideMethod>> mHideMethods;

    public HideSeekerDelegate() {
        mHideMethods = new HashMap<>();
    }

    @NonNull
    public Map<String, List<HideMethod>> getHideMethods() {
        return mHideMethods;
    }

    @Nullable
    public List<HideMethod> getHideMethods(@NonNull String className) {
        return mHideMethods.get(className);
    }

    public boolean isAnnotatedByHide(@NonNull String className, @NonNull HideMethod hideMethod) {
        List<HideMethod> hideMethods = getHideMethods(className);
        if (hideMethods == null) {
            return false;
        }
        for (HideMethod it: hideMethods) {
            if (it.equals(hideMethod)) {
                return true;
            }
        }
        return false;
    }

    public void addHideMethod(@NonNull String className, @NonNull HideMethod hideMethod) {
        List<HideMethod> hideMethods = getHideMethods(className);
        if (hideMethods == null) {
            hideMethods = new ArrayList<>();
        }
        hideMethods.add(hideMethod);
        if (!mHideMethods.containsKey(className)) {
            mHideMethods.put(className, hideMethods);
        }
    }
}
