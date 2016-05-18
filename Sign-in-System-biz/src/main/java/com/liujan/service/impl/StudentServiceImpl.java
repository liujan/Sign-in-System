package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.*;
import com.liujan.mapper.FaceMapper;
import com.liujan.mapper.InfoMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.StudentService;
import com.liujan.util.DateUtil;
import com.liujan.util.FaceUtil;
import com.liujan.util.FileUtil;
import com.liujan.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("studentService")
public class StudentServiceImpl implements StudentService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private StudentExample studentExample = new StudentExample();
    private StatisticExample statisexample = new StatisticExample();
    private FaceExample faceExample = new FaceExample();

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private StatisticMapper statisticMapper;
    @Autowired
    private FaceMapper faceMapper;
    @Autowired
    private InfoMapper infoMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result<Void> siginIn(String stuId, String pwd, HttpServletRequest request) {
        Result<Void> result = new Result<Void>();
        try {
            String osVersion = request.getHeader("User-Agent");
            //用电脑签到
            if (osVersion.contains("Macintosh") || osVersion.contains("macintosh") || ((osVersion.contains("Windows")
                    || osVersion.contains("windows")) && (osVersion.contains("NT") || osVersion.contains("nt") || osVersion.contains("Nt")))) {
                return result.status(Result.Status.DEVICE_ERROR);
            }

            String macAddr = NetUtil.getMacAddress(request.getRemoteAddr());
            Student student = studentMapper.selectByPrimaryKey(stuId);
            if (student == null)
                return result.status(Result.Status.USER_ERROR); //未注册
            else if (student.getMacAddress() != null && student.getMacAddress().length() > 2
                    && !student.getMacAddress().equals(macAddr) && !student.getMacAddress().equals("00"))
                return result.status(Result.Status.DEVICE_ERROR); //换手机了
            else if (student.getUserPwd() != null && !student.getUserPwd().equals(pwd))
                return result.status(Result.Status.PWD_ERROR); //密码不对
            else {
                Info info = infoMapper.selectByPrimaryKey(1);
                int courseId = info.getCourseId();
                int week = info.getWeek();

                statisexample.clear();
                statisexample.or().andCourseIdEqualTo(courseId).andStuIdEqualTo(stuId).andWeekEqualTo(week);
                List<Statistic> statisticsList = statisticMapper.selectByExample(statisexample);
                if (!statisticsList.isEmpty()) {
                    request.getSession().setAttribute("stuId", stuId);
                    request.getSession().setAttribute("courseId", courseId);
                    return result.status(Result.Status.SUCCESS); //已签到
                }

                if (student.getMacAddress().equals("00")) {
                    student.setMacAddress(macAddr);
                    studentMapper.updateByPrimaryKeySelective(student);
                }
                
                Statistic statistic = new Statistic();
                statistic.setStuId(stuId);
                statistic.setCourseId(courseId);
                statistic.setSiginTime(DateUtil.format(new Date()));
                statistic.setWeek(week);
                statistic.setConfidence(1.0);
                int count = statisticMapper.insertSelective(statistic);
                if (count > 0) {
                    request.getSession().setAttribute("stuId", stuId);
                    request.getSession().setAttribute("courseId", courseId);
                    return result.status(Result.Status.SUCCESS);
                }
                else
                    return result.status(Result.Status.ERROR);
            }
        } catch (Exception e) {
            logger.error("sign in error!", e);
            return result.status(Result.Status.ERROR);
        }
    }

    @Override
    public Result<Void> register(Student student, HttpServletRequest request) {
        Result<Void> result = new Result<Void>();
        try {
            String osVersion = request.getHeader("User-Agent");
            if (osVersion.contains("Macintosh") || osVersion.contains("macintosh") || ((osVersion.contains("Windows")
                    || osVersion.contains("windows")) && (osVersion.contains("NT") || osVersion.contains("nt") || osVersion.contains("Nt")))) {
                return result.status(Result.Status.ERROR);
            }

            String stuId = student.getStuId();
            Student record = studentMapper.selectByPrimaryKey(stuId);
            if (record != null) {
                return result.status(Result.Status.ID_ERROR); //该用户已被注册
            } else {
                String macAddr = NetUtil.getMacAddress(request.getRemoteAddr());
                student.setMacAddress(macAddr);
                int count = studentMapper.insertSelective(student);
                if (count > 0)
                    return result.status(Result.Status.SUCCESS);
                else
                    return result.status(Result.Status.ERROR);
            }
        } catch (Exception e) {
            logger.error("register error!", e);
            return result.status(Result.Status.ERROR);
        }
    }

    @Override
    public Result<Void> login(String stuId, String pwd) {
        Result<Void> result = new Result<Void>();
        try {
            studentExample.clear();
            studentExample.or().andStuIdEqualTo(stuId).andUserPwdEqualTo(pwd);
            List<Student> studentList = studentMapper.selectByExample(studentExample);
            if (studentList == null || studentList.size() == 0)
                return result.status(Result.Status.ERROR);
            else
                return result.status(Result.Status.SUCCESS);
        } catch (Exception e) {
            logger.error("login error!", e);
            return result.status(Result.Status.ERROR);
        }

    }

    @Override
    public Student getStudentById(String stuId) {
        Student student = studentMapper.selectByPrimaryKey(stuId);
        return student;
    }

    @Override
    public Result<List<Student>> listStudents() {
        Result<List<Student>> result = new Result<List<Student>>();
        try {
            studentExample.clear();
            studentExample.or();
            List<Student> studentList = studentMapper.selectByExample(studentExample);
            return result.status(Result.Status.SUCCESS).data(studentList);
        }
        catch (Exception e) {
            return result.status(Result.Status.ERROR);
        }
    }

    @Override
    public List<Student> listSiginedStudent(List<Statistic> statisticList) {
        ArrayList<Student> studentList = new ArrayList<Student>();
        for (Statistic statistic : statisticList) {
            String stuId = statistic.getStuId();
            String stime = statistic.getSiginTime();
            double con = 0;
            if (statistic.getConfidence() != null) {
                con = statistic.getConfidence();
            }
            Student student = studentMapper.selectByPrimaryKey(stuId);
            if (student != null) {
                student.setEmail(stime);
                student.setMacAddress(String.valueOf(con));
                student.setUserPwd("");
                studentList.add(student);
            }
        }

        return studentList;
    }

    @Override
    public Result<Void> changePhone(String stuId) {
        Result<Void> result = new Result<Void>();
        try {
            Student student = studentMapper.selectByPrimaryKey(stuId);
            if (student != null) {
                student.setMacAddress("00");
                int count = studentMapper.updateByPrimaryKeySelective(student);
                if (count > 0)
                    return result.status(Result.Status.SUCCESS);
                else
                    return result.status(Result.Status.ERROR);
            } else
                return result.status(Result.Status.USER_ERROR);
        }
        catch (Exception e) {
            logger.error("change student phone error!", e);
            return result.status(Result.Status.ERROR);
        }
    }

    @Override
    public Result<Void> updateInfo(Student student) {
        Result<Void> result = new Result<Void>();
        try {
            int count = studentMapper.updateByPrimaryKeySelective(student);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e){
            logger.error("update student info error!", e);
            return result.status(Result.Status.ERROR);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result<Void> deleteStudentById(String stuId) {
        Result<Void> result = new Result<Void>();
        try {
            boolean faceResult = FaceUtil.removePerson(stuId);
            if (faceResult) {
                statisexample.clear();
                statisexample.or().andStuIdEqualTo(stuId);
                statisticMapper.deleteByExample(statisexample);
                //删除该学生的所有照片
                faceExample.clear();
                faceExample.or().andStuIdEqualTo(stuId);
                faceMapper.deleteByExample(faceExample);

                File originDir = new File(Constant.IMAGE_PATH + stuId);
                File compressDir = new File(Constant.COMPRESS_IMAGE_PATH + stuId);
                FileUtil.deleteDir(originDir);
                FileUtil.deleteDir(compressDir);
                int count = studentMapper.deleteByPrimaryKey(stuId);
                if (count > 0)
                    return result.status(Result.Status.SUCCESS);
                else
                    return result.status(Result.Status.ERROR);
            }
            return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("delete student error!", e);
            return result.status(Result.Status.ERROR);
        }

    }

}
