package com.liujan.service;

import com.liujan.entity.Course;
import com.liujan.entity.Student;

import java.text.ParseException;
import java.util.List;

public interface CourseService {
	public Course getCourseById(int courseId);
	public List<Course> getCourseList(int teacherId);
	public int deleteCourseById(int courseId, int teacherId);
	public int addCourse(String courseName, String beginTime, String endTime, int dayInWeek, int teacherId, List<String> stuIdList) throws ParseException;
	public List<Course> getAllCourseList(List<Integer> teacherList);
	public List<String> getStudentByCourseId(int courseId);
    public int deleteCourseStudent(String stuId, int courseId);
    public int addStudentToCourse(List<String> stuIdList, int courseId);
}
