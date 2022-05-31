package com.zhang.oa.dao;

import com.zhang.oa.entity.ProcessFlow;

import java.util.List;

public interface ProcessFlowDao {
    Integer insert(ProcessFlow processFlow);
    void update(ProcessFlow processFlow);
    List<ProcessFlow> selectByFormId(Long fromId);
}
