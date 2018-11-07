package com.yeoh.mock.sub;

import android.app.Activity;
import com.yeoh.mock.MockModel;
import com.yeoh.mock.MockModel2;

public class Caller extends Activity {

    private static void callMockModel() {
        MockModel mockModel = new MockModel();
        mockModel.mockTestMethod();
        mockModel.mockTestMethod(1);
        mockModel.mockTestMethod("mock");
        mockModel.mockTestMethod();
    }

    private static void callMockModel2() {
        MockModel2 mockModel2 = new MockModel2();
        mockModel2.abc();
        System.out.print(mockModel2.add(1, 2) + "");
        String first = mockModel2.getFirst("a", "b", "c");
        System.out.print(first + "");
    }

    public static void mock() {
        callMockModel();
        callMockModel2();
    }

    private void test(String arg0) {
    }
}
