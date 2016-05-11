package com.liujan.service;

import com.liujan.domain.Result;
import com.liujan.entity.Statistic;
import com.liujan.entity.Student;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StudentService {
	public Result<Void> siginIn(String stuId, String pwd, HttpServletRequest request);
	public Result<Void> register(Student student, HttpServletRequest request);
	public Result<Void> login(String stuId, String pwd);
	public Student getStudentById(String stuId);
	public Result<List<Student>> listStudents();
	public List<Student> listSiginedStudent(List<Statistic> statisticList);
	public Result<Void> changePhone(String stuId);
	public Result<Void> updateInfo(Student student);
	public Result<Void> deleteStudentById(String stuId);
}
