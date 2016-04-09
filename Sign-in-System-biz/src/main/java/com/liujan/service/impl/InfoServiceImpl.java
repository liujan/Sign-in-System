package com.liujan.service.impl;


import com.liujan.entity.Info;
import com.liujan.entity.InfoExample;
import com.liujan.mapper.InfoMapper;
import com.liujan.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("infoService")
public class InfoServiceImpl implements InfoService {
	private InfoExample infoExample = new InfoExample();
	@Autowired
	private InfoMapper infoMapper;
	
	@Override
	public int getCourseId() {
		infoExample.clear();
		infoExample.or();
		List<Info> infoList = infoMapper.selectByExample(infoExample);
		
		if (infoList.isEmpty())
			return -1; //未设置当前课程
		else {
			Date currentDate = new Date();
			Date date = infoList.get(0).getCurrent();
			int dif = (int)Math.abs((currentDate.getTime() - date.getTime()) / 86400000L);
			if (dif < 1)
				return infoList.get(0).getCourseId();
			else {
				return -1;
			}
		}
		
	}
	
	@Override
	public int getWeek() {
		Info info = infoMapper.selectByPrimaryKey(1);
		
		if (info == null  || info.getWeek() == 0)
			return -1; //未设置当前课程
		else {
			Date currentDate = new Date();
			Date date = info.getCurrent();
			int dif = (int)Math.abs((currentDate.getTime() - date.getTime()) / 86400000L);
			
			if (dif < 1)
				return info.getWeek();
			else {
				return -1;
			}
		}
		
	}

	@Override
	public int setCourseId(int courseId, int week, int teacherId) {
		Info info = infoMapper.selectByPrimaryKey(1);
		int result = 0;
		Date currentDate = new Date();
		if (info == null) {
			info = new Info();
			info.setCourseId(courseId);
			info.setWeek(week);
			info.setId(1);
			info.setCurrent(currentDate);
			info.setTeacherId(teacherId);
			result = infoMapper.insertSelective(info);
		}
		else {
			info.setCourseId(courseId);
			info.setWeek(week);
			info.setId(1);
			info.setCurrent(currentDate);
			info.setTeacherId(teacherId);
			result = infoMapper.updateByPrimaryKeySelective(info);
		}
		
		if (result == 1)
			return 1;
		else
			return -1;
	}
}
