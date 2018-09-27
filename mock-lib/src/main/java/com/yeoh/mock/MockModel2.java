package com.yeoh.mock;

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
}
