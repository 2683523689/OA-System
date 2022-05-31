package com.zhang.oa.utils;

import org.junit.Test;

public class MyBatisUtilsTestor {

    @Test
    public void testCase1(){
        String result= (String) MyBatisUtils.executeQuery(sqlSession -> {
            return sqlSession.<String>selectOne("test.sample");
        });
        System.out.println(result);
    }
}
