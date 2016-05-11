package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.*;
import com.liujan.mapper.CourseMapper;
import com.liujan.mapper.InfoMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.CourseService;
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
	public Result<List<Course>> getCourseListByTeacherId(int teacherId) {
		Result<List<Course>> result = new Result<List<Course>>();
		try {
			courseExample.clear();
			courseExample.or().andTeacherIdEqualTo(teacherId);
			List<Course> courseList = courseMapper.selectByExample(courseExample);
			return result.status(Result.Status.SUCCESS).data(courseList);
		}
		catch (Exception e) {
			logger.error("get course list by teacherId error!", e);
			return result.status(Result.Status.ERROR);
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public Result<Void> deleteCourseById(int courseId, int teacherId) {
        Result<Void> result = new Result<Void>();
        try {
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
            int count = courseMapper.deleteByPrimaryKey(courseId);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("delete course by id error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public Result<Void> addCourse(Course course, List<String> stuIdList) {
		Result<Void> result = new Result<Void>();
        try {
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
            int count =  courseMapper.insertSelective(course);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("add course error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
	public List<Course> getAllCourseList(List<Integer> teacherList) {
		courseExample.clear();
		courseExample.or().andTeacherIdIn(teacherList);
		return courseMapper.selectByExample(courseExample);
	}

	@Override
	public Result<List<String>> getStudentByCourseId(int courseId) {
        Result<List<String>> result = new Result<List<String>>();
        try {
            Course course = courseMapper.selectByPrimaryKey(courseId);
            if (course == null || course.getStudentList() == null
                    || course.getStudentList().isEmpty()) {
                return result.status(Result.Status.SUCCESS);
            }
            String[] stuIds = course.getStudentList().split(Constant.courseStuIdSeperator);
            return result.status(Result.Status.SUCCESS).data(Arrays.asList(stuIds));
        }
        catch (Exception e) {
            logger.error("get student by courseId error!", e);
            return result.status(Result.Status.ERROR);
        }

	}

    @Override
    public Result<Void> deleteCourseStudent(String stuId, int courseId) {
        Result<Void> result = new Result<Void>();
        try {
            Course course = courseMapper.selectByPrimaryKey(courseId);
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
            int count =  courseMapper.updateByPrimaryKeySelective(course);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("delete course student error!", e);
            return result.status(Result.Status.ERROR);
        }

    }

    @Override
    public Result<Void> addStudentToCourse(List<String> stuIdList, int courseId) {
        Result<Void> result = new Result<Void>();
        try {
            Course course = courseMapper.selectByPrimaryKey(courseId);
            if (course.getStudentList() != null) {
                String[] stuIds = course.getStudentList().split(Constant.courseStuIdSeperator);
                for (String stuId : stuIds) {
                    if (!stuIdList.contains(stuId) && stuId.trim().length() > 0) {
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
            int count = courseMapper.updateByPrimaryKeySelective(course);
            if (count > 0)
                return result.status(Result.Status.SUCCESS);
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("add student to course error!", e);
            return result.status(Result.Status.ERROR);
        }
    }
}
