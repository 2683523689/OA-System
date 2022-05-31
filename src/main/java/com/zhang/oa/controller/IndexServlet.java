package com.zhang.oa.controller;

import com.zhang.oa.entity.Department;
import com.zhang.oa.entity.Employee;
import com.zhang.oa.entity.Node;
import com.zhang.oa.entity.User;
import com.zhang.oa.service.DepartmentService;
import com.zhang.oa.service.EmployeeService;
import com.zhang.oa.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet", urlPatterns = "/index")
public class IndexServlet extends HttpServlet {
    Logger logger = LoggerFactory.getLogger(IndexServlet.class);
    private final UserService userService = new UserService();
    private final EmployeeService employeeService = new EmployeeService();
    private final DepartmentService departmentService = new DepartmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("login_user");
        Employee employee = employeeService.selectById(user.getEmployeeId());
        Department department = departmentService.selectById(employee.getDepartmentId());
        List<Node> nodeList = userService.selectNodeByUserId(user.getUserId());
        req.setAttribute("node_list", nodeList);
        session.setAttribute("current_employee", employee);
        session.setAttribute("current_department", department);
        req.getRequestDispatcher("/index.ftl").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
