package com.zhang.oa.controller;

import com.alibaba.fastjson.JSON;
import com.zhang.oa.entity.User;
import com.zhang.oa.service.UserService;
import com.zhang.oa.service.exception.BussinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = "/check_login")
public class LoginServlet extends HttpServlet {
    Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        // 接受用户输入
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Map<String, Object> result = new HashMap<>();
        // 调用业务逻辑
        try {
            User user = userService.checkLogin(username, password);
            // 向当前会话session存入登录的用户信息
            HttpSession session = req.getSession();
            session.setAttribute("login_user", user);
            result.put("code", "0");
            result.put("message", "success");
            result.put("redirect_url", "/index");
        } catch (BussinessException e) {
            logger.error(e.getMessage(), e);
            result.put("code", e.getCode());
            result.put("message", e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("code", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
        }
        // 返回数据
        String jsonString = JSON.toJSONString(result);
        resp.getWriter().println(jsonString);

    }
}
