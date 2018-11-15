package com.yeoh.mock.sub;

import android.app.Activity;

public class SubCaller extends Activity {

    public static void mock() {
        SubMockModel mockModel = new SubMockModel();
        mockModel.subMockTestMethod();
        mockModel.subMockTestMethod(1);
        mockModel.subMockTestMethod("hello seeker");
        mockModel.subMockTestMethod(1,"hello",null);
    }

    private void test(String arg0) {
    }
}
