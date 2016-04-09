package com.liujan.service.impl;

import com.liujan.entity.Teacher;
import com.liujan.entity.TeacherExample;
import com.liujan.mapper.TeacherMapper;
import com.liujan.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("teacherService")
public class TeacherServiceImpl implements TeacherService {
	private TeacherExample teacherExample = new TeacherExample();
	@Autowired
	private TeacherMapper teacherMapper;
	
	public int login(String name, String pwd) {
		teacherExample.clear();
		teacherExample.or().andUserNameEqualTo(name).andUserPwdEqualTo(pwd);
		List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
		if (teacherList != null && !teacherList.isEmpty()) {
			int teacherId = teacherList.get(0).getTeacherId();
			return teacherId;
		}
		else
			return -1;
	}

	@Override
	public int register(String name, String teacherName, String email, String pwd) {
		teacherExample.clear();
		teacherExample.or().andEmailEqualTo(email);
		List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
		if (teacherList != null && !teacherList.isEmpty())
			return -1; //该邮箱已被注册
		
		teacherList = null;
		teacherExample.clear();
		teacherExample.or().andUserNameEqualTo(name);
		teacherList = teacherMapper.selectByExample(teacherExample);
		if (teacherList != null && !teacherList.isEmpty())
			return -2; //该用户名已被注册
		
		Teacher teacher = new Teacher();
		teacher.setUserName(name);
		teacher.setTeacherName(teacherName);
		teacher.setEmail(email);
		teacher.setUserPwd(pwd);
		
		return teacherMapper.insertSelective(teacher);
	}

	@Override
	public Teacher getMyInfo(int teacherId) {
		return teacherMapper.selectByPrimaryKey(teacherId);
	}

	@Override
	public int updateInfo(int teacherId, String userName, String teacherName, String email,
			String userPwd) {
		Teacher teacher= teacherMapper.selectByPrimaryKey(teacherId);
		if (teacher != null) {
			teacher.setUserName(userName);
			teacher.setTeacherName(teacherName);
			teacher.setEmail(email);
			teacher.setUserPwd(userPwd);
			return teacherMapper.updateByPrimaryKeySelective(teacher);
		}
		else {
			return -1;
		}
	}

	@Override
	public List<Teacher> getTeacherList() {
		teacherExample.clear();
		teacherExample.or();
		return teacherMapper.selectByExample(teacherExample);
	}

}
