package com.liujan.service;

import com.liujan.entity.Teacher;

import java.util.List;

public interface TeacherService {
	public int login(String name, String pwd);
	public int register(String name, String teacherName, String email, String pwd);
	public Teacher getMyInfo(int teacherId);
	public int updateInfo(int teacherId, String userName, String teacherName, String email, String userPwd);
	public List<Teacher> getTeacherList();
}
