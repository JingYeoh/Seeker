package com.yeoh.example;

import com.yeoh.mock.sub.Caller;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws ClassNotFoundException {
        Caller.mock();
        assert int[].class == Class.forName("[I");
        assert String[].class == Class.forName("[Ljava.lang.String;");
    }
}