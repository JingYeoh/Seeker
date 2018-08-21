package com.yeoh.seeker.processer;

import java.util.List;

/**
 * Used to save method info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class SeekerMethod {

    final String methodName;
    final List<String> params;

    public SeekerMethod(String methodName, List<String> params) {
        this.methodName = methodName;
        this.params = params;
    }
}
