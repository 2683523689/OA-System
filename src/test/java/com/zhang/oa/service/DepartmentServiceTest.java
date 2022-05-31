package com.zhang.oa.service;

import com.zhang.oa.entity.Department;
import junit.framework.TestCase;

public class DepartmentServiceTest extends TestCase {

    public void testselectById(){

        DepartmentService departmentService=new DepartmentService();
        Department department = departmentService.selectById(1L);
        System.out.println(department);
    }

}