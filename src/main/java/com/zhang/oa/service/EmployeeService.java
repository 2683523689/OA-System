package com.zhang.oa.service;

import com.zhang.oa.dao.EmployeeDao;
import com.zhang.oa.entity.Employee;
import com.zhang.oa.utils.MyBatisUtils;

public class EmployeeService {

    /**
     * 按编号查找用户
     *
     * @param employeeId 员工编号
     * @return 员工对象，不存在时返回null
     */
    public Employee selectById(Long employeeId) {
        return (Employee) MyBatisUtils.executeQuery(sqlSession -> sqlSession.getMapper(EmployeeDao.class).selectById(employeeId));
    }
}
