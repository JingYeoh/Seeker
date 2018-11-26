package com.yeoh.mock;

import android.app.Fragment;

public class Caller extends Fragment {

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
