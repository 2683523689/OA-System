package com.zhang.oa.utils;

import junit.framework.TestCase;

public class MD5UtilsTest extends TestCase {
    public void test(){
        String test = MD5Utils.md5Digest("test");
        System.out.println(test);
    }

}