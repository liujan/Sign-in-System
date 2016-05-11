package com.liujan.service.impl;


import com.liujan.domain.Result;
import com.liujan.entity.Info;
import com.liujan.entity.InfoExample;
import com.liujan.mapper.InfoMapper;
import com.liujan.service.InfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("infoService")
public class InfoServiceImpl implements InfoService {
	private InfoExample infoExample = new InfoExample();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
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
	public Result<Void> setCourseId(int courseId, int week, int teacherId) {
		Result<Void> result = new Result<Void>();
		try {
			Info info = infoMapper.selectByPrimaryKey(1);
			int count = 0;
			Date currentDate = new Date();
			if (info == null) {
				info = new Info();
				info.setCourseId(courseId);
				info.setWeek(week);
				info.setId(1);
				info.setCurrent(currentDate);
				info.setTeacherId(teacherId);
				count = infoMapper.insertSelective(info);
			}
			else {
				info.setCourseId(courseId);
				info.setWeek(week);
				info.setId(1);
				info.setCurrent(currentDate);
				info.setTeacherId(teacherId);
				count = infoMapper.updateByPrimaryKeySelective(info);
			}
			if (count == 1)
				return result.status(Result.Status.SUCCESS);
			else
				return result.status(Result.Status.ERROR);
		}
        catch (Exception e) {
            logger.error("set course error!", e);
            return result.status(Result.Status.ERROR);
        }
	}
}
