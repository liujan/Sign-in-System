package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.entity.*;
import com.liujan.mapper.CourseMapper;
import com.liujan.mapper.InfoMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.CourseService;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service("courseService")
public class CourseServiceImpl implements CourseService {
	private Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

	private CourseExample courseExample = new CourseExample();
	private StatisticExample statisticExample = new StatisticExample();
	private InfoExample infoExample = new InfoExample();
	private StudentExample studentExample = new StudentExample();
	@Autowired
	private CourseMapper courseMapper;
	@Autowired
	private StatisticMapper statisticMapper;
	@Autowired
	private InfoMapper infoMapper;
	@Autowired
	private StudentMapper studentMapper;

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
			int dayInWeek, int teacherId, List<String> stuIdList) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Course course = new Course();
		course.setCourseName(courseName);
		course.setBeginTime(sdf.parse(beginTime));
		course.setEndTime(sdf.parse(endTime));
		course.setDayInWeek(dayInWeek);
		course.setTeacherId(teacherId);
		StringBuilder sb = new StringBuilder();
		if (stuIdList != null) {
            for (String stuId : stuIdList) {
                sb.append(stuId + Constant.courseStuIdSeperator);
            }
           if (sb.toString().endsWith(Constant.courseStuIdSeperator)) {
               sb.deleteCharAt(sb.length()-1);
           }
            course.setStudentList(sb.toString());
        }
		return courseMapper.insertSelective(course);
	}

	@Override
	public List<Course> getAllCourseList(List<Integer> teacherList) {
		courseExample.clear();
		courseExample.or().andTeacherIdIn(teacherList);
		return courseMapper.selectByExample(courseExample);
	}

	@Override
	public List<String> getStudentByCourseId(int courseId) {
		Course course = courseMapper.selectByPrimaryKey(courseId);
		if (course == null || course.getStudentList() == null
				|| course.getStudentList().isEmpty()) {
			return new ArrayList<String>();
		}
		String[] stuIds = course.getStudentList().split(Constant.courseStuIdSeperator);
		return  Arrays.asList(stuIds);
	}

    @Override
    public int deleteCourseStudent(String stuId, int courseId) {
        System.out.println(stuId);
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (course == null || course.getStudentList() == null) {
            return 0;
        }
        String[] stuIds = course.getStudentList().split(Constant.courseStuIdSeperator);
        StringBuilder newStuIdsb = new StringBuilder();
        for (String id : stuIds) {
            if (!id.equals(stuId)) {
                newStuIdsb.append(id + Constant.courseStuIdSeperator);
            }
        }
        String newStuIds = newStuIdsb.toString();
        if (!newStuIds.isEmpty()) {
            newStuIds = newStuIds.substring(0, newStuIds.length()-1);
        }
        course.setStudentList(newStuIds);
        return courseMapper.updateByPrimaryKeySelective(course);
    }

    @Override
    public int addStudentToCourse(List<String> stuIdList, int courseId) {
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (course == null || stuIdList == null) {
            return 0;
        }

        if (course.getStudentList() != null) {
            String[] stuIds = course.getStudentList().split(Constant.courseStuIdSeperator);
            for (String stuId : stuIds) {
                if (!stuIdList.contains(stuId)) {
                    stuIdList.add(stuId);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String stuId : stuIdList) {
            sb.append(stuId + Constant.courseStuIdSeperator);
        }
        String newStuids = sb.toString();
        if (!newStuids.isEmpty()) {
            newStuids = newStuids.substring(0, newStuids.length()-1);
        }
        course.setStudentList(newStuids);
        return courseMapper.updateByPrimaryKeySelective(course);
    }
}
