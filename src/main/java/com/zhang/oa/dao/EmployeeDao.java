package com.zhang.oa.dao;

import com.zhang.oa.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeDao {

    Employee selectById(Long employeeId);

    Employee selectLeader(@Param("emp") Employee employee);
}
