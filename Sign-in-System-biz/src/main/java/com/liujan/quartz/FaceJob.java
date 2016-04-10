package com.liujan.quartz;

import com.liujan.constant.Constant;
import com.liujan.entity.Face;
import com.liujan.service.FaceService;
import com.liujan.util.FaceUtil;
import com.liujan.util.FileUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class FaceJob implements Job{
	private FaceService faceService;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			faceService = (FaceService) arg0.getScheduler().getContext().get("faceService");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		List<Face> faceList = faceService.listAllFace();
		Map<String, List<String>> stuPhotoList = new HashMap<String, List<String>>();
		if (faceList != null) {
			int count = 0;
			for (Face face : faceList) {
				if (count > Constant.PHOTO_NUM) {
					break;
				}
				String stuId = face.getStuId();
				String photo = face.getPhoto();
				String faceId = face.getFaceId();
				List<String> photoList = null;
				
				if (!FileUtil.fileExists(Constant.IMAGE_PATH + stuId + "/" + photo)
						|| !FileUtil.fileExists(Constant.COMPRESS_IMAGE_PATH + stuId + "/" + photo)) {
					faceService.deletePhoto(stuId, photo);
					FileUtil.deleteDir(new File(Constant.IMAGE_PATH + stuId + "/" + photo));
					FileUtil.deleteDir(new File(Constant.COMPRESS_IMAGE_PATH + stuId + "/" + photo));
					continue;
				}
				if (faceId != null && !faceId.equals("")) {
					continue;
				}
				if (stuPhotoList.containsKey(stuId)) {
					photoList = stuPhotoList.get(stuId);
				}
				else {
					photoList = new ArrayList<String>();
				}
				photoList.add(photo);
				stuPhotoList.put(stuId, photoList);
				count++;
			}
			for (Entry<String, List<String>> entry : stuPhotoList.entrySet()) {
				String stuId = entry.getKey();
				List<String> photoList = entry.getValue();
				Map<String, String> photoFaceIdMap = FaceUtil.addPhotoToGroup(photoList, stuId);
				for (Entry<String, String> photoFaceId : photoFaceIdMap.entrySet()) {
					String photo = photoFaceId.getKey();
					String faceId = photoFaceId.getValue();
					if (faceId != null && !faceId.trim().equals("")) {
						faceService.updateFaceId(photo, faceId);
					}
					else {
						File originFile = new File(Constant.IMAGE_PATH + stuId + "/" + photo);
						File compressFile = new File(Constant.COMPRESS_IMAGE_PATH + stuId + "/" + photo);
						FileUtil.deleteDir(originFile);
						FileUtil.deleteDir(compressFile);
						faceService.deletePhoto(stuId, photo);
					}
				}
			}
		}
		
	}
	
}
