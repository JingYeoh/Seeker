package com.yeoh.mock.sub;

import android.content.Context;
import com.yeoh.seeker.annotation.Hide;
import com.yeoh.seeker.annotation.Modifier;

public class SubMockModel {

    @Hide
    public void subMockTestMethod() {
    }

    @Hide
    public void subMockTestMethod(String name) {
    }

    @Hide(Modifier.DEFAULT)
    public void subMockTestMethod(Context context) {
    }

    @Hide(Modifier.PUBLIC)
    public void subMockTestMethod(int num) {
    }

    @Hide(Modifier.PROTECTED)
    void subMockTestMethod(int num, String name, Context context) {
    }
}
