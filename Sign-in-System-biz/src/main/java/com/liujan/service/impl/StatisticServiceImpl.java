package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.StatisticExample;
import com.liujan.mapper.CourseMapper;
import com.liujan.mapper.StatisticMapper;
import com.liujan.mapper.StudentMapper;
import com.liujan.service.StatisticService;
import com.liujan.util.DateUtil;
import com.liujan.util.FaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service("statisticService")
public class StatisticServiceImpl implements StatisticService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private StatisticExample statisticExample = new StatisticExample();
	@Autowired
	private StatisticMapper statisticMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private StudentMapper studentMapper;

	@Override
	public Result<List<Statistic>> getSiginList(int courseId, int week) {
		Result<List<Statistic>> result = new Result<List<Statistic>>();
		try {
			Course course = courseMapper.selectByPrimaryKey(courseId);
			if (course.getStudentList() == null || course.getStudentList().isEmpty())
				return result.status(Result.Status.STUDENT_ERROR).data(new ArrayList<Statistic>());
			statisticExample.clear();
			statisticExample.or().andCourseIdEqualTo(courseId).andWeekEqualTo(week);
			List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
			return result.status(Result.Status.SUCCESS).data(statisticList);
		}
		catch (Exception e) {
			logger.error("get signinlist error!");
			return result.status(Result.Status.ERROR).data(new ArrayList<Statistic>());
		}
	}

	@Override
	public List<Statistic> getSiginListByStuId(String stuId) {
		statisticExample.clear();
		statisticExample.or().andStuIdEqualTo(stuId);
		return statisticMapper.selectByExample(statisticExample);
	}

	@Override
	public Result<String> photoSigin(int courseId, int week, MultipartFile file) {
        Result<String> result = new Result<String>();
        try {
            if (file != null) {
                Date date = new Date();
                int r = (int) (Math.random() * 1000);
                String originalName = file.getOriginalFilename();
                String suffix = originalName.substring(originalName.lastIndexOf(".")); //文件后缀名
                String fileName = date.getTime() + "_" + r + suffix;
                String path = Constant.IMAGE_PATH + Constant.TEACHER_IMAGE_PATH + fileName;
                //判断图片文件夹和压缩图片的文件夹是否存在，不存在则创建
                File isImageExists = new File(Constant.IMAGE_PATH + Constant.TEACHER_IMAGE_PATH);
                if (!isImageExists.isDirectory()) {
                    isImageExists.mkdirs();
                }
                File isCompressExists = new File(Constant.COMPRESS_IMAGE_PATH + Constant.TEACHER_IMAGE_PATH);
                if (!isCompressExists.exists()) {
                    isCompressExists.mkdirs();
                }
                File localFile = new File(path);

                // 写文件到本地
                file.transferTo(localFile);

                Map<String, Double> studentMap = FaceUtil.recognition(fileName, Constant.TEACHER_IMAGE_PATH);

                fileName = file.getOriginalFilename();

                for(Map.Entry<String, Double> student : studentMap.entrySet()) {
                    statisticExample.clear();
                    String stuId = student.getKey();
                    double con = student.getValue();
                    statisticExample.or().andStuIdEqualTo(stuId).andCourseIdEqualTo(courseId).andWeekEqualTo(week);
                    List<Statistic> statisticList = statisticMapper.selectByExample(statisticExample);
                    if (statisticList != null && statisticList.size() != 0) {
                        Statistic record = statisticList.get(0);
                        if ((record.getConfidence() - con) > 0.0001) {
                            record.setConfidence(con);
                            statisticMapper.updateByPrimaryKeySelective(record);
                        }
                    }
                    else {
                        Statistic record = new Statistic();
                        record.setStuId(stuId);
                        record.setSiginTime(DateUtil.format(new Date()));
                        record.setCourseId(courseId);
                        record.setConfidence(con);
                        record.setWeek(week);
                        statisticMapper.insertSelective(record);
                    }
                }
                return result.status(Result.Status.SUCCESS).data(fileName);
            }
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("photo signin error!", e);
            return result.status(Result.Status.ERROR).data(file.getOriginalFilename());
        }
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

	public Result<List<String>> getUnSiginStuIdList(int courseId, int week) {
        Result<List<String>> result = new Result<List<String>>();
        try {
            Course course = courseMapper.selectByPrimaryKey(courseId);
            if (course.getStudentList() == null || course.getStudentList().isEmpty()) {
                return result.status(Result.Status.STUDENT_ERROR);
            }
            List<String> stuIdList = Arrays.asList(course.getStudentList().split(Constant.courseStuIdSeperator));
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
            return result.status(Result.Status.SUCCESS).data(unSiginStuIdList);
        }
        catch (Exception e) {
            logger.error("get unsignin stuId list error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

}
