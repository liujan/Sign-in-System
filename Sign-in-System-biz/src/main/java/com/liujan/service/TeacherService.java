package com.liujan.service;

import com.liujan.domain.Result;
import com.liujan.entity.Teacher;

import java.util.List;

public interface TeacherService {
	public Result<Integer> login(String name, String pwd);
	public Result<Void> register(Teacher teacher);
	public Result<Teacher> getMyInfo(int teacherId);
	public Result<Void> updateInfo(Teacher teacher);
	public List<Teacher> getTeacherList();
}
