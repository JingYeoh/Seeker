package com.yeoh.mock;

import android.content.Context;

import com.yeoh.seeker.annotation.Hide;
import com.yeoh.seeker.annotation.Modifier;

public class MockModel {

    @Hide
    public void mockTestMethod() {
    }

    @Hide
    public void mockTestMethod(String name) {
    }

    @Hide
    public void mockTestMethod(Context context) {
    }

    @Hide(Modifier.PROTECTED)
    public void mockTestMethod(int num) {
    }
}
