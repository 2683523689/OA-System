package com.zhang.oa.dao;

import com.zhang.oa.entity.LeaveForm;
import com.zhang.oa.utils.MyBatisUtils;
import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LeaveFormDaoTest extends TestCase {

    public void testInsert() {
        LeaveForm form=new LeaveForm();
        form.setEmployeeId(41L);
        form.setFormType(1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startTime=simpleDateFormat.parse("2020-03-25 08:00:00");
            Date endTime=simpleDateFormat.parse("2020-04-05 09:01:02");
            form.setStartTime(startTime);
            form.setEndTime(endTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        form.setReason("回家探亲");
        form.setCreateTime(new Date());
        form.setState("processing");

        Integer i = (Integer) MyBatisUtils.executeUpdate(sqlSession -> sqlSession.getMapper(LeaveFormDao.class).insert(form));
        System.out.println(i);
    }

    public void testTestInsert() {
    }

    public void testSelectByParams() {
        List<Map> list = (List<Map>) MyBatisUtils.executeQuery(sqlSession -> sqlSession.getMapper(LeaveFormDao.class).selectByParams("process", 2L));
        System.out.println(list);
    }
}