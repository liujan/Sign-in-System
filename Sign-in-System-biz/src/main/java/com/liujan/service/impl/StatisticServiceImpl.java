package com.liujan.service.impl;

import com.liujan.entity.Statistic;
import com.liujan.entity.StatisticExample;
import com.liujan.mapper.StatisticMapper;
import com.liujan.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {
	private StatisticExample statisticExample = new StatisticExample();
	@Autowired
	private StatisticMapper statisticMapper;
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
			statisticExample.or().andStuIdEqualTo(stuId).andCourseIdEqualTo(courseId).andWeekEqualTo(week);
			List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
			if (statisticList != null && statisticList.size() != 0) {
				Statistic record = statisticList.get(0);
				if (record.getConfidence() < con) {
					record.setConfidence(con);
				}
				result += statisticMapper.updateByPrimaryKey(record);
			}
			else {
				Statistic record = new Statistic();
				record.setStuId(stuId);
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
		for (Statistic statistic : statisticList) {
			String stuId = statistic.getStuId();
			int week = statistic.getWeek();
			List<Integer> siginWeekList = null;
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
		return studentMap;
	}

}