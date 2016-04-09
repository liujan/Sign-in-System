package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.entity.*;
import com.liujan.mapper.FaceMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.StudentService;
import com.liujan.util.FaceUtil;
import com.liujan.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("studentService")
public class StudentServiceImpl implements StudentService {
	private StudentExample studentExample = new StudentExample();
	private StatisticExample statisexample = new StatisticExample();
	private FaceExample faceExample = new FaceExample();
	
	@Autowired
	private StudentMapper studentMapper;
	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private FaceMapper faceMapper;
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public int SiginIn(String stuId, String pwd, String macAddress, int courseId, int week) {
		// TODO Auto-generated method stub
		Student student = studentMapper.selectByPrimaryKey(stuId);
		if (student == null) 
			return -1; //未注册
		else if (student.getMacAddress() != null && student.getMacAddress().length() > 2 
				&& !student.getMacAddress().equals(macAddress) && !student.getMacAddress().equals("00"))
			return -2; //换手机了
		else if (student.getUserPwd() != null && !student.getUserPwd().equals(pwd))
			return -3; //姓名不对
		else {
			statisexample.clear();
			statisexample.or().andCourseIdEqualTo(courseId).andStuIdEqualTo(stuId).andWeekEqualTo(week);
			List<Statistic> statisticsList= statisticMapper.selectByExample(statisexample);
			if (!statisticsList.isEmpty()) {
				return 1; //已签到
			}
			
			if (student.getMacAddress().equals("00")) {
				student.setMacAddress(macAddress);
				studentMapper.updateByPrimaryKeySelective(student);
			}
			
			Date date = new Date();
			Statistic statistic = new Statistic();
			statistic.setStuId(stuId);
			statistic.setCourseId(courseId);
			statistic.setSiginTime(date);
			statistic.setWeek(week);
			return statisticMapper.insertSelective(statistic);
		}
	}

	public int Register(String stuId, String name, String macAddr, String pwd, String email) {
		Student student = studentMapper.selectByPrimaryKey(stuId);
		if (student != null) {
			return -1; //该用户已被注册
		}
		else {
			student = new Student();
			student.setStuId(stuId);
			student.setName(name);
			student.setMacAddress(macAddr);
			student.setUserPwd(pwd);
			student.setEmail(email);
			return  studentMapper.insertSelective(student);
		}
	}

	@Override
	public int login(String stuId, String pwd) {
		studentExample.clear();
		studentExample.or().andStuIdEqualTo(stuId).andUserPwdEqualTo(pwd);
		List<Student> studentList = studentMapper.selectByExample(studentExample);
		if (studentList != null && !studentList.isEmpty())
			return 1;
		else 
			return -1;
	}

	@Override
	public Student getStudentById(String stuId) {
		Student student = studentMapper.selectByPrimaryKey(stuId);
		return student;
	}

	@Override
	public List<Student> listStudents() {
		studentExample.clear();
		studentExample.or();
		return studentMapper.selectByExample(studentExample);
	}

	@Override
	public List<Student> listSiginedStudent(List<Statistic> statisticList) {
		ArrayList<Student> studentList = new ArrayList<Student>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Statistic statistic : statisticList) {
			String stuId = statistic.getStuId();
			String stime = sdf.format(statistic.getSiginTime());
			double con = 0;
			if (statistic.getConfidence() != null) {
				con = statistic.getConfidence();
			}
			Student student = studentMapper.selectByPrimaryKey(stuId);
			if (student != null) {
				student.setEmail(stime);
				if (con > 0) {
					student.setMacAddress(String.valueOf(con));
				}
				else {
					student.setMacAddress("网页签到");
				}
				student.setUserPwd("");
				studentList.add(student);
			}
		}
		
		return studentList;
	}

	@Override
	public int changePhone(String stuId) {
		Student student = studentMapper.selectByPrimaryKey(stuId);
		if (student != null) {
			student.setMacAddress("00");
			return studentMapper.updateByPrimaryKeySelective(student);
		}
		else 
			return -1;
	}

	@Override
	public int updateInfo(String stuId, String name, String email,
			String userPwd) {
		Student student = new Student();
		student.setStuId(stuId);
		student.setName(name);
		student.setEmail(email);
		student.setUserPwd(userPwd);
		return studentMapper.updateByPrimaryKeySelective(student);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public boolean deleteStudentById(String stuId) {
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
			return studentMapper.deleteByPrimaryKey(stuId) > 0;
		}
		return false;
	}
	
}
