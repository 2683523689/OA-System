package com.zhang.oa.dao;

import com.zhang.oa.entity.Node;
import com.zhang.oa.utils.MyBatisUtils;

import java.util.List;

public class RbacDao {

    public List<Node> selectNodeByUserId(Long userId) {
        return (List<Node>) MyBatisUtils.executeQuery(sqlSession -> sqlSession.selectList("rbacmapper.selectNodeByUserId", userId));
    }
}
