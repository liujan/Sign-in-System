package com.liujan.quartz;

import com.liujan.constant.Constant;
import com.liujan.quartz.FaceJob;
import com.liujan.service.FaceService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class FaceScheduler {
	@Autowired
	private FaceService faceService;
	
	public void addPhotoToGroup() {
		JobDetail jobDetail = JobBuilder.newJob(FaceJob.class)
			    .withIdentity("addPhotoToGroupJob", Constant.GROUP_NAME)
			    .build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("addPhotoToGroupTrigger", Constant.GROUP_NAME)
				.startAt(new Date())
				.withSchedule(simpleSchedule().withIntervalInSeconds(8).repeatForever())
				.build();
		try {
			System.out.println("job start");
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.getContext().put("faceService", faceService);
			 scheduler.scheduleJob(jobDetail, trigger);    
		     scheduler.start();
		     
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
