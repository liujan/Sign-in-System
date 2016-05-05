package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.StatisticExample;
import com.liujan.mapper.CourseMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {
	private StatisticExample statisticExample = new StatisticExample();
	@Autowired
	private StatisticMapper statisticMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StudentMapper studentMapper;

	@Override
	public List<Statistic> getSiginList(int courseId, int week) {
		statisticExample.clear();
		statisticExample.or().andCourseIdEqualTo(courseId).andWeekEqualTo(week);
		
		return statisticMapper.selectByExample(statisticExample);
	}
	@Override
	public List<Statistic> getSiginListByStuId(String stuId) {
		statisticExample.clear();
		statisticExample.or().andStuIdEqualTo(stuId);
		return statisticMapper.selectByExample(statisticExample);
	}
	@Override
	public int photoSigin(int courseId, int week, Map<String, Double> studentMap) {
		int result = 0;
		if (studentMap == null) {
			return result;
		}
		for(Map.Entry<String, Double> student : studentMap.entrySet()) {
			statisticExample.clear();
			String stuId = student.getKey();
			double con = student.getValue();
            String stuName = studentMapper.selectByPrimaryKey(stuId).getName();
			statisticExample.or().andStuIdEqualTo(stuId).andCourseIdEqualTo(courseId).andWeekEqualTo(week);
			List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
			if (statisticList != null && statisticList.size() != 0) {
				Statistic record = statisticList.get(0);
				if ((record.getConfidence() - con) > 0.0001) {
					record.setConfidence(con);
				}
				result += statisticMapper.updateByPrimaryKey(record);
			}
			else {
				Statistic record = new Statistic();
				record.setStuId(stuId);
                record.setStuName(stuName);
				record.setSiginTime(new Date());
				record.setCourseId(courseId);
				record.setConfidence(con);
				record.setWeek(week);
				result += statisticMapper.insertSelective(record);
			}
		}
		return result;
	}
	@Override
	public Map<String, List<Integer>> getSiginListByCourse(int courseId) {
		statisticExample.clear();
		statisticExample.or().andCourseIdEqualTo(courseId);
		List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
		Map<String, List<Integer>> studentMap = new HashMap<String, List<Integer>>();
        Course course = courseMapper.selectByPrimaryKey(courseId);
        List<String> stuIdList = new ArrayList<String>();
        if (course.getStudentList() != null && course.getStudentList().trim().length() > 0) {
            stuIdList = new ArrayList<String>( Arrays.asList(course.getStudentList().split(Constant.courseStuIdSeperator)));
        }

		for (Statistic statistic : statisticList) {
			String stuId = statistic.getStuId();
			int week = statistic.getWeek();
			List<Integer> siginWeekList = null;
            if (stuIdList != null && stuIdList.contains(stuId)) {
                stuIdList.remove(stuId);
            }
			if (studentMap.containsKey(stuId)) {
				siginWeekList = studentMap.get(stuId);
			}
			else {
				siginWeekList = new ArrayList<Integer>();
			}
			if (!siginWeekList.contains(week)) {
				siginWeekList.add(week);
			}
			studentMap.put(stuId, siginWeekList);
		}
        for (String stuId : stuIdList) {
            studentMap.put(stuId, new ArrayList<Integer>());
        }
		return studentMap;
	}

	public List<String> getUnSiginStuIdList(int courseId, int week, List<String> stuIdList) {
		statisticExample.clear();
		statisticExample.or().andCourseIdEqualTo(courseId).andWeekEqualTo(week);
		List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
		List<String> unSiginStuIdList = new ArrayList<String>();
		for (String stuId : stuIdList) {
			boolean flag = true;
			for (Statistic statistic : statisticList) {
				if (statistic.getStuId().equals(stuId)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				unSiginStuIdList.add(stuId);
			}
		}
		return unSiginStuIdList;
	}

}
