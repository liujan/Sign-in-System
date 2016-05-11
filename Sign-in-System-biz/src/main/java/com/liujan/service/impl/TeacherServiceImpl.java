package com.liujan.service.impl;

import com.liujan.domain.Result;
import com.liujan.entity.Teacher;
import com.liujan.entity.TeacherExample;
import com.liujan.mapper.TeacherMapper;
import com.liujan.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("teacherService")
public class TeacherServiceImpl implements TeacherService {
	private TeacherExample teacherExample = new TeacherExample();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TeacherMapper teacherMapper;
	
	public Result<Integer> login(String name, String pwd) {
		Result<Integer> result = new Result<Integer>();
        try {
            teacherExample.clear();
            teacherExample.or().andUserNameEqualTo(name).andUserPwdEqualTo(pwd);
            List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
            if (teacherList != null && !teacherList.isEmpty()) {
                int teacherId = teacherList.get(0).getTeacherId();
                return result.status(Result.Status.SUCCESS).data(teacherId);
            }
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("teacher login error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public Result<Void> register(Teacher teacher) {
        Result<Void> result = new Result<Void>();
        try {
            teacherExample.clear();
            teacherExample.or().andEmailEqualTo(teacher.getEmail());
            List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
            if (teacherList != null && !teacherList.isEmpty())
                return result.status(Result.Status.EMAIL_ERROR); //该邮箱已被注册

            teacherList = null;
            teacherExample.clear();
            teacherExample.or().andUserNameEqualTo(teacher.getUserName());
            teacherList = teacherMapper.selectByExample(teacherExample);
            if (teacherList != null && !teacherList.isEmpty())
                return result.status(Result.Status.USER_ERROR); //该用户名已被注册

            int count =  teacherMapper.insertSelective(teacher);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public Result<Teacher> getMyInfo(int teacherId) {
        Result<Teacher> result = new Result<Teacher>();
        try {
            Teacher teacher = teacherMapper.selectByPrimaryKey(teacherId);
            return result.status(Result.Status.SUCCESS).data(teacher);
        }
        catch (Exception e) {
            logger.error("get teacher info error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public Result<Void> updateInfo(Teacher teacher) {
        Result<Void> result = new Result<Void>();
        try {
            int count = teacherMapper.updateByPrimaryKeySelective(teacher);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public List<Teacher> getTeacherList() {
		teacherExample.clear();
		teacherExample.or();
		return teacherMapper.selectByExample(teacherExample);
	}

}
