package com.zhang.oa.dao;

import com.zhang.oa.entity.ProcessFlow;
import com.zhang.oa.utils.MyBatisUtils;
import junit.framework.TestCase;

import java.util.Date;

public class ProcessFlowDaoTest extends TestCase {

    public void testInsert() {
        ProcessFlow flow=new ProcessFlow();
        flow.setFormId(31L);
        flow.setOperatorId(21L);
        flow.setAction("audit");
        flow.setReason("approved");
        flow.setCreateTime(new Date());
        flow.setAuditTime(new Date());
        flow.setOrderNo(1);
        flow.setState("ready");
        flow.setIsLast(1);
        Integer i = (Integer) MyBatisUtils.executeUpdate(sqlSession -> sqlSession.getMapper(ProcessFlowDao.class).insert(flow));
        System.out.println(i);
    }
}