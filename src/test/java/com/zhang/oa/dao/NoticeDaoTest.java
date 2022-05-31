package com.zhang.oa.dao;

import com.zhang.oa.entity.Notice;
import com.zhang.oa.utils.MyBatisUtils;
import junit.framework.TestCase;

import java.util.Date;

public class NoticeDaoTest extends TestCase {

    public void testInsert() {
        Notice notice=new Notice();
        notice.setContent("同意");
        notice.setCreateTime(new Date());
        notice.setReceiverId(1L);
        Integer i= (Integer) MyBatisUtils.executeUpdate(sqlSession -> sqlSession.getMapper(NoticeDao.class).insert(notice));
        System.out.println(i);
    }
}