package com.zhang.oa.controller;

import com.alibaba.fastjson.JSON;
import com.zhang.oa.entity.Notice;
import com.zhang.oa.entity.User;
import com.zhang.oa.service.NoticeService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet", urlPatterns = "/notice/list")
public class NoticeServlet extends HttpServlet {
    private NoticeService noticeService=new NoticeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        User user = (User) req.getSession().getAttribute("login_user");
        List<Notice> noticeList = noticeService.getNoticeList(user.getEmployeeId());
        Map<String, Object> result = new HashMap<>();
        result.put("code", "0");
        result.put("message", "");
        result.put("count", noticeList.size());
        result.put("data", noticeList);
        String jsonString = JSON.toJSONString(result);
        resp.getWriter().println(jsonString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
