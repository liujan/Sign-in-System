package com.liujan.service;

import com.liujan.domain.Result;
import com.liujan.entity.Course;
import com.liujan.entity.Student;

import java.text.ParseException;
import java.util.List;

public interface CourseService {
	public Course getCourseById(int courseId);
	public Result<List<Course>> getCourseListByTeacherId(int teacherId);
	public Result<Void> deleteCourseById(int courseId, int teacherId);
	public Result<Void> addCourse(Course course, List<String> stuIdList);
	public List<Course> getAllCourseList(List<Integer> teacherList);
	public Result<List<String>> getStudentByCourseId(int courseId);
    public Result<Void> deleteCourseStudent(String stuId, int courseId);
    public Result<Void> addStudentToCourse(List<String> stuIdList, int courseId);
}
