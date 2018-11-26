package com.yeoh.mock;

import android.content.Context;
import com.yeoh.seeker.annotation.Hide;
import com.yeoh.seeker.annotation.Modifier;

public class MockModel2 {

    @Hide
    public String test() {
        return "test";
    }

    @Hide
    public String test(String value) {
        return value;
    }

    @Hide(Modifier.PROTECTED)
    public int add(int a, int b) {
        return a + b;
    }

    @Hide(Modifier.DEFAULT)
    public String[] test(String... value) {
        return value;
    }

    @Hide(Modifier.PUBLIC)
    public Context getContext(Context context) {
        return context;
    }

    @Hide
    public Context[] getContexts(Context... contexts) {
        return contexts;
    }

    @Hide
    public String getFirst(String... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        return strings[0];
    }

    @Hide
    public String[] abc() {
        return new String[]{"a", "b", "c"};
    }
}
