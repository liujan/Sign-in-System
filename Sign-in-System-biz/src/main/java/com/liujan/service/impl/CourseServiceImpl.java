package com.liujan.service.impl;

import com.liujan.entity.*;
import com.liujan.mapper.CourseMapper;
import com.liujan.mapper.InfoMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service("courseService")
public class CourseServiceImpl implements CourseService {
	private Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
	private CourseExample courseExample = new CourseExample();
	private StatisticExample statisticExample = new StatisticExample();
	private InfoExample infoExample = new InfoExample();
	@Autowired
	private CourseMapper courseMapper;
	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private InfoMapper infoMapper;

	public  Course getCourseById(int courseId) {
		Course course = courseMapper.selectByPrimaryKey(courseId);
		if (course != null)
			return course;
		else {
			logger.error("no such course where course id=" + String.valueOf(courseId) );
			return null;
		}
	}

	@Override
	public List<Course> getCourseList(int teacherId) {
		courseExample.clear();
		courseExample.or().andTeacherIdEqualTo(teacherId);
		return courseMapper.selectByExample(courseExample);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public int deleteCourseById(int courseId, int teacherId) {
		statisticExample.clear();
		statisticExample.or().andCourseIdEqualTo(courseId);
		statisticMapper.deleteByExample(statisticExample);
		
		infoExample.clear();
		infoExample.or().andCourseIdEqualTo(courseId).andTeacherIdEqualTo(teacherId);
		List<Info> infoList = infoMapper.selectByExample(infoExample);
		if (infoList != null && !infoList.isEmpty()) {
			for (Info info : infoList) {
				int infoId = info.getId();
				infoMapper.deleteByPrimaryKey(infoId);
			}
		}
		
		return courseMapper.deleteByPrimaryKey(courseId);
	}

	@Override
	public int addCourse(String courseName, String beginTime, String endTime,
			int dayInWeek, int teacherId) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Course course = new Course();
		course.setCourseName(courseName);
		course.setBeginTime(sdf.parse(beginTime));
		course.setEndTime(sdf.parse(endTime));
		course.setDayInWeek(dayInWeek);
		course.setTeacherId(teacherId);
		
		return courseMapper.insertSelective(course);
	}

	@Override
	public List<Course> getAllCourseList(List<Integer> teacherList) {
		courseExample.clear();
		courseExample.or().andTeacherIdIn(teacherList);
		return courseMapper.selectByExample(courseExample);
	}

}
