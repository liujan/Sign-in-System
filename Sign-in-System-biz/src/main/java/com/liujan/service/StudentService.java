package com.liujan.service;

import com.liujan.entity.Statistic;
import com.liujan.entity.Student;

import java.util.List;

public interface StudentService {
	public int SiginIn(String stuId, String name, String macAddress, int courseId, int week);
	public int Register(String stuId, String name, String macAddr, String pwd, String email);
	public int login(String stuId, String pwd);
	public Student getStudentById(String stuId);
	public List<Student> listStudents();
	public List<Student> listSiginedStudent(List<Statistic> statisticList);
	public int changePhone(String stuId);
	public int updateInfo(String stuId, String name, String email, String userPwd);
	public boolean deleteStudentById(String stuId);
}
