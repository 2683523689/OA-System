package com.zhang.oa.service;

import com.zhang.oa.entity.Node;
import junit.framework.TestCase;

import java.util.List;

public class UserServiceTest extends TestCase {
    private UserService userService=new UserService();

    public void testCheckLogin1() {
        userService.checkLogin("uu","");
        userService.checkLogin("m8","1234");
        userService.checkLogin("m8","f57e762e3fb7e1e3ec8ec4db6a1248e1");
    }

    public void testselectNodeByUserId(){
        List<Node> nodeList = userService.selectNodeByUserId(21L);
        System.out.println(nodeList);

    }

}