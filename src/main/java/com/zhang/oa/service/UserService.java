package com.zhang.oa.service;

import com.zhang.oa.dao.RbacDao;
import com.zhang.oa.dao.UserDao;
import com.zhang.oa.entity.Node;
import com.zhang.oa.entity.User;
import com.zhang.oa.service.exception.BussinessException;
import com.zhang.oa.utils.MD5Utils;

import java.util.List;

/**
 * @author Gavin
 */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final RbacDao rbacDao = new RbacDao();

    /**
     * 根据前台输入进行密码校验
     *
     * @param username 用户名
     * @param password 密码
     * @return User实体类
     * @throws BussinessException L001--用户名不存在,L002--密码错误
     */
    public User checkLogin(String username, String password) {
        User user = userDao.selectByUsername(username);
        if (user == null) {
            // 抛出用户不存在异常
            throw new BussinessException("L001", "用户名不存在");
        }

        String md5 = MD5Utils.md5Digest(password, user.getSalt());
        if (!md5.equals(user.getPassword())) {
            throw new BussinessException("L002", "密码错误");
        }
        return user;
    }

    public List<Node> selectNodeByUserId(Long userId) {
        List<Node> nodeList = rbacDao.selectNodeByUserId(userId);
        return nodeList;
    }
}
