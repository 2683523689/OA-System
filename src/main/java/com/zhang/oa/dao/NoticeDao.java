package com.zhang.oa.dao;

import com.zhang.oa.entity.Notice;

import java.util.List;

public interface NoticeDao {

    Integer insert(Notice notice);
    List<Notice> selectByReceiverId(Long receiverId);
}
