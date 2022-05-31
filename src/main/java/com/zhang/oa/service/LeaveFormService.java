package com.zhang.oa.service;

import com.zhang.oa.dao.EmployeeDao;
import com.zhang.oa.dao.LeaveFormDao;
import com.zhang.oa.dao.NoticeDao;
import com.zhang.oa.dao.ProcessFlowDao;
import com.zhang.oa.entity.Employee;
import com.zhang.oa.entity.LeaveForm;
import com.zhang.oa.entity.Notice;
import com.zhang.oa.entity.ProcessFlow;
import com.zhang.oa.service.exception.BussinessException;
import com.zhang.oa.utils.MyBatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaveFormService {
    /**
     */
    public LeaveForm createLeaveForm(LeaveForm form) {

        LeaveForm savedForm = (LeaveForm) MyBatisUtils.executeUpdate(sqlSession -> {
            // 1. 持久化form表单数据，8级一下状态processing，8级以上approved
            Employee employee = sqlSession.getMapper(EmployeeDao.class).selectById(form.getEmployeeId());
            if (employee.getLevel() == 8) {
                form.setState("approved");
            } else {
                form.setState("processing");
            }
            sqlSession.getMapper(LeaveFormDao.class).insert(form);

            // 2. 增加第一条流程数据，表单已提交，状态complete
            ProcessFlow flow1 = new ProcessFlow();
            flow1.setFormId(form.getFormId());
            flow1.setOperatorId(employee.getEmployeeId());
            flow1.setAction("apply");
            flow1.setCreateTime(new Date());
            flow1.setOrderNo(1);
            flow1.setState("complete");
            flow1.setIsLast(0);
            sqlSession.getMapper(ProcessFlowDao.class).insert(flow1);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HH时");
            // 3. 分情况创建其余流程数据
            if (employee.getLevel() < 7) {
                Employee dmanager = sqlSession.getMapper(EmployeeDao.class).selectLeader(employee);
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(form.getFormId());
                flow2.setOperatorId(dmanager.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                long diffTime = form.getEndTime().getTime() - form.getStartTime().getTime();
                float hours = diffTime / (1000 * 60 * 60) * 1f;
                if (hours >= BusinessConstants.MANAGER_AUDIT_HOURS) {
                    flow2.setIsLast(0);
                    sqlSession.getMapper(ProcessFlowDao.class).insert(flow2);
                    Employee manager = sqlSession.getMapper(EmployeeDao.class).selectLeader(dmanager);
                    ProcessFlow flow3 = new ProcessFlow();
                    flow3.setFormId(form.getFormId());
                    flow3.setOperatorId(manager.getEmployeeId());
                    flow3.setAction("audit");
                    flow3.setCreateTime(new Date());
                    flow3.setOrderNo(3);
                    flow3.setState("ready");
                    flow3.setIsLast(1);
                    sqlSession.getMapper(ProcessFlowDao.class).insert(flow3);
                } else {
                    flow2.setIsLast(1);
                    sqlSession.getMapper(ProcessFlowDao.class).insert(flow2);
                }

                String noticeContent=String.format("您的请假申请[%s-%s]已提交,请等待上级审批.",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(employee.getEmployeeId(),noticeContent));
                noticeContent=String.format("%s-%s提起请假申请[%s-%s],请尽快审批",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(dmanager.getEmployeeId(),noticeContent));

            } else if (employee.getLevel() == 7) {
                Employee manager = sqlSession.getMapper(EmployeeDao.class).selectLeader(employee);
                ProcessFlow flow4 = new ProcessFlow();
                flow4.setFormId(form.getFormId());
                flow4.setOperatorId(manager.getEmployeeId());
                flow4.setAction("audit");
                flow4.setCreateTime(new Date());
                flow4.setOrderNo(2);
                flow4.setState("process");
                flow4.setIsLast(1);
                sqlSession.getMapper(ProcessFlowDao.class).insert(flow4);

                String noticeContent=String.format("您的请假申请[%s-%s]已提交,请等待上级审批.",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(employee.getEmployeeId(),noticeContent));
                noticeContent=String.format("%s-%s提起请假申请[%s-%s],请尽快审批",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(manager.getEmployeeId(),noticeContent));
            } else if (employee.getLevel() == 8) {
                ProcessFlow flow5 = new ProcessFlow();
                flow5.setFormId(form.getFormId());
                flow5.setOperatorId(employee.getEmployeeId());
                flow5.setAction("audit");
                flow5.setResult("approved");
                flow5.setReason("自动通过");
                flow5.setCreateTime(new Date());
                flow5.setAuditTime(new Date());
                flow5.setOrderNo(2);
                flow5.setState("complete");
                flow5.setIsLast(1);
                sqlSession.getMapper(ProcessFlowDao.class).insert(flow5);

                String noticeContent=String.format("您的请假申请[%s-%s]系统已经自动批准通过.",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(employee.getEmployeeId(),noticeContent));
            }
            return form;
        });

        return savedForm;
    }

    public List<Map> getLeaveFormList(String pfState, Long operatorId) {
        return (List<Map>) MyBatisUtils.executeQuery(sqlSession -> sqlSession.getMapper(LeaveFormDao.class).selectByParams(pfState, operatorId));
    }

    public void audit(Long formId,Long operatorId,String result,String reason){
        MyBatisUtils.executeUpdate(sqlSession -> {
            // 1. 改变当前任务状态
            List<ProcessFlow> flowList = sqlSession.getMapper(ProcessFlowDao.class).selectByFormId(formId);
            if(flowList.size()==0){
                throw new BussinessException("PF001","无效的审批流程");
            }

            List<ProcessFlow> processList = flowList.stream().filter(p -> p.getOperatorId() == operatorId && p.getState().equals("process")).collect(Collectors.toList());
            ProcessFlow processFlow=null;
            if(processList.size()==0){
                throw new BussinessException("PF002","未找到待处理任务");
            }else {
                processFlow=processList.get(0);
                processFlow.setState("complete");
                processFlow.setResult(result);
                processFlow.setReason(reason);
                processFlow.setAuditTime(new Date());
                sqlSession.getMapper(ProcessFlowDao.class).update(processFlow);
            }

            // 2. if 任务为最后节点流程结束，请假单状态变为approved或refused
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HH时");
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            LeaveForm form = leaveFormDao.selectById(formId);
            Employee employee = employeeDao.selectById(form.getEmployeeId());
            Employee operator=employeeDao.selectById(operatorId);

            if (processFlow.getIsLast()==1){
                form.setState(result);
                leaveFormDao.update(form);

                String strResult=null;
                if (result.equals("aproved")){
                    strResult="批准";
                } else if (result.equals("refused")) {
                    strResult="驳回";
                }
                String noticeContent=String.format("您的请假申请[%s-%s]%s%s已%s,,审批意见:%s ,审批流程已结束",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),operator.getTitle(),operator.getName(),strResult,reason);
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));
                noticeContent=String.format("%s-%s提起请假申请[%s-%s]您已%s,审批意见:%s,审批流程已结束",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),strResult,reason);
                sqlSession.getMapper(NoticeDao.class).insert(new Notice(operator.getEmployeeId(),noticeContent));

            }else {
                // 3. 非最后节点，审批通过，更改下一节点
                List<ProcessFlow> readyList = flowList.stream().filter(p -> p.getState().equals("ready")).collect(Collectors.toList());
                if(result.equals("approved")){
                    ProcessFlow readyProcess = readyList.get(0);
                    readyProcess.setState("process");
                    sqlSession.getMapper(ProcessFlowDao.class).update(readyProcess);

                    String noticeContent=String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),operator.getTitle(),operator.getName(),reason);
                    sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));
                    noticeContent=String.format("%s-%s提起请假申请[%s-%s],请尽快审批",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()));
                    sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));
                    noticeContent=String.format("%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),reason);
                    sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));

                } else if (result.equals("refused")) {
                    for (ProcessFlow p:readyList){
                        p.setState("cancel");
                        sqlSession.getMapper(ProcessFlowDao.class).update(p);
                    }
                    form.setState("refused");
                    leaveFormDao.update(form);
                    String noticeContent=String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s ,审批流程已结束",dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),operator.getTitle(),operator.getName(),reason);
                    sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));
                    noticeContent=String.format("%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束",employee.getTitle(),employee.getName(),dateFormat.format(form.getStartTime()),dateFormat.format(form.getEndTime()),reason);
                    sqlSession.getMapper(NoticeDao.class).insert(new Notice(form.getEmployeeId(),noticeContent));
                }
            }
            return null;
        });
    }

}
