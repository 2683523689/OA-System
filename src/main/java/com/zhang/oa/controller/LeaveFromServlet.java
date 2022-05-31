package com.zhang.oa.controller;

import com.alibaba.fastjson.JSON;

import com.zhang.oa.service.LeaveFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zhang.oa.entity.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LeaveFromServlet", urlPatterns = "/leave/*")
public class LeaveFromServlet extends HttpServlet {

    private LeaveFormService leaveFormService = new LeaveFormService();
    private Logger logger = LoggerFactory.getLogger(LeaveFromServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        String uri = req.getRequestURI();
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        if (methodName.equals("create")) {
            this.create(req, resp);
        } else if (methodName.equals("list")) {
            this.getLeaveFormList(req, resp);
        } else if (methodName.equals("audit")) {
            this.audit(req, resp);

        }
    }

    /**
     * 创建请假单
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("login_user");
        String formType = req.getParameter("formType");
        String strStartTime = req.getParameter("startTime");
        String strEndTime = req.getParameter("endTime");
        String reason = req.getParameter("reason");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");

        LeaveForm form = new LeaveForm();

        Map<String, Object> result = new HashMap();
        try {
            form.setEmployeeId(user.getEmployeeId());
            form.setStartTime(dateFormat.parse(strStartTime));
            form.setEndTime(dateFormat.parse(strEndTime));
            form.setFormType(Integer.parseInt(formType));
            form.setReason(reason);
            form.setCreateTime(new Date());
            leaveFormService.createLeaveForm(form);
            result.put("code", "0");
            result.put("message", "success");

        } catch (Exception e) {
            logger.error("请假申请异常", e);
            result.put("code", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
        }
        String json = JSON.toJSONString(result);
        resp.getWriter().println(json);
    }

    /**
     * 查询需要审核的请假单列表
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void getLeaveFormList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("login_user");
        List<Map> formList = leaveFormService.getLeaveFormList("process", user.getEmployeeId());
        Map result = new HashMap();
        result.put("code", 0);
        result.put("message", "");
        result.put("count", formList.size());
        result.put("data", formList);
        String jsonString = JSON.toJSONString(result);
        response.getWriter().println(jsonString);
    }

    private void audit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String formId = request.getParameter("formId");
        String result = request.getParameter("result");
        String reason = request.getParameter("reason");
        User user = (User) request.getSession().getAttribute("login_user");
        Map resultMap = new HashMap();
        try {
            leaveFormService.audit(Long.valueOf(formId),user.getEmployeeId(),result,reason);
            resultMap.put("code","0");
            resultMap.put("message","success");
        }catch (Exception e){
            logger.error("请假单审核失败",e);
            resultMap.put("code", e.getClass().getSimpleName());
            resultMap.put("message", e.getMessage());
        }
        String jsonString = JSON.toJSONString(resultMap);
        response.getWriter().println(jsonString);
    }
}
