package com.zhang.oa.dao;

import com.zhang.oa.entity.Department;

public interface DepartmentDao {

    Department selectById(Long departmentId);
}
