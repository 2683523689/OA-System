package com.zhang.oa.service;

import com.zhang.oa.dao.DepartmentDao;
import com.zhang.oa.entity.Department;
import com.zhang.oa.utils.MyBatisUtils;

public class DepartmentService {

    /**
     * 按编号查找部门
     *
     * @param departmentId 部门编号
     * @return 部门对象，不存在时返回null
     */
    public Department selectById(Long departmentId) {
        return (Department) MyBatisUtils.executeQuery(sqlSession -> sqlSession.getMapper(DepartmentDao.class).selectById(departmentId));
    }
}
