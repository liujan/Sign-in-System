package com.liujan.service;

import com.liujan.domain.Result;

public interface InfoService {
	public int getCourseId();
	public int getWeek();
	public String getCourseName();
	public Result<Void> setCourseId(int courseId, int week, int teacherId);
}
