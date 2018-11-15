package com.yeoh.example;

import com.yeoh.mock.MockModel2;
import com.yeoh.mock._MockModel2RefDelegate;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws ClassNotFoundException {
        assert int[].class == Class.forName("[I");
        assert String[].class == Class.forName("[Ljava.lang.String;");

        Object var2 = null;
        _MockModel2RefDelegate var3 = null;
        var3 = new _MockModel2RefDelegate(new MockModel2());
        var3.abc();
        System.out.print(var3.add(1, 2) + "");
        String first = var3.getFirst(new String[]{"a", "b", "c"});
        System.out.print(first + "");
    }
}