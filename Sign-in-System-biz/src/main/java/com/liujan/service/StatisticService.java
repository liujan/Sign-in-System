package com.liujan.service;

import com.liujan.entity.Statistic;

import java.util.List;
import java.util.Map;

public interface StatisticService {
	public List<Statistic> getSiginList(int courseId, int week);
	public List<Statistic> getSiginListByStuId(String stuId);
	public int photoSigin(int courseId, int week, Map<String, Double> studentMap);
	public Map<String, List<Integer>> getSiginListByCourse(int courseId);
	
}
