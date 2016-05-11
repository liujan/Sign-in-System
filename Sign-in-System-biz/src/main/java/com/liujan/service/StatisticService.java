package com.liujan.service;

import com.liujan.domain.Result;
import com.liujan.entity.Statistic;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StatisticService {
	public Result<List<Statistic>> getSiginList(int courseId, int week);
	public List<Statistic> getSiginListByStuId(String stuId);
	public Result<String> photoSigin(int courseId, int week , MultipartFile file);
	public Map<String, List<Integer>> getSiginListByCourse(int courseId);
	public Result<List<String>> getUnSiginStuIdList(int courseId, int week);
	
}
