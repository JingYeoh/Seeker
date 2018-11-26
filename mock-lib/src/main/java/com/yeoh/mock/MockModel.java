package com.yeoh.mock;

import android.content.Context;
import com.yeoh.seeker.annotation.Hide;
import com.yeoh.seeker.annotation.Modifier;

public class MockModel {

    @Hide
    public void mockTestMethod() {
        System.out.println("mockTestMethod()");
    }

    @Hide
    public void mockTestMethod(String name) {
        System.out.println("mockTestMethod (" + name + ")");
    }

    @Hide
    public void mockTestMethod(Context context) {
        System.out.println("mockTestMethod with context params");
    }

    @Hide(Modifier.PROTECTED)
    public void mockTestMethod(int num) {
        System.out.println("mockTestMethod (" + num + ")");
    }
}
