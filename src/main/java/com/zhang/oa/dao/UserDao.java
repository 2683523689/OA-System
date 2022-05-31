package com.zhang.oa.dao;

import com.zhang.oa.entity.User;
import com.zhang.oa.utils.MyBatisUtils;

public class UserDao {

    /**
     * 根据用户名查询用户表
     *
     * @param userName
     * @return
     */
    public User selectByUsername(String userName) {
        User user = (User) MyBatisUtils.executeQuery(sqlSession -> sqlSession.selectOne("usermapper.selectByUsername", userName));
        return user;
    }
}
