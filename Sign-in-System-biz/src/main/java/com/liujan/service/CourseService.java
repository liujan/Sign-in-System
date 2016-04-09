package com.liujan.service;

import com.liujan.entity.Course;

import java.text.ParseException;
import java.util.List;

public interface CourseService {
	public Course getCourseById(int courseId);
	public List<Course> getCourseList(int teacherId);
	public int deleteCourseById(int courseId, int teacherId);
	public int addCourse(String courseName, String beginTime, String endTime, int dayInWeek, int teacherId) throws ParseException;
	public List<Course> getAllCourseList(List<Integer> teacherList);
}
