package com.zhang.oa.service;

import com.zhang.oa.dao.NoticeDao;
import com.zhang.oa.entity.Notice;
import com.zhang.oa.utils.MyBatisUtils;

import java.util.List;

public class NoticeService {
    public List<Notice> getNoticeList(Long receiverId){
        return  (List<Notice>) MyBatisUtils.executeQuery(sqlSession -> sqlSession.getMapper(NoticeDao.class).selectByReceiverId(receiverId));
    }
}
