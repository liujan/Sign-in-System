package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.entity.Face;
import com.liujan.entity.FaceExample;
import com.liujan.mapper.FaceMapper;
import com.liujan.service.FaceService;
import com.liujan.util.FaceUtil;
import com.liujan.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("faceService")
public class FaceServiceImpl implements FaceService {
	@Autowired
	private FaceMapper faceMapper;
	private FaceExample faceExample = new FaceExample();

	@Override
	public List<Face> listAllFace() {
		faceExample.clear();
		return faceMapper.selectByExample(faceExample);
	}

	@Override
	public boolean updateFaceId(String photo, String faceId) {
		Face record = faceMapper.selectByPrimaryKey(photo);
		if (record != null) {
			record.setFaceId(faceId);
			return faceMapper.updateByPrimaryKey(record) > 0;
		}
		return false;
	}

	@Override
	public List<Face> listEmptyFace() {
		faceExample.clear();
		faceExample.or().andFaceIdIsNull();
		return faceMapper.selectByExample(faceExample);
	}

	@Override
	public boolean addPhotoByStuId(String stuId, String photo) {
		Face record = new Face();
		record.setPhoto(photo);
		record.setStuId(stuId);
		int result =  faceMapper.insertSelective(record);
		return result > 0;
	}

	@Override
	public List<String> listPhotoByStuId(String stuId) {
		if (stuId != null) {
			faceExample.clear();
			faceExample.or().andStuIdEqualTo(stuId);
			List<Face> faceList = faceMapper.selectByExample(faceExample);
			
			if (faceList != null) {
				List<String> photoList = new ArrayList<String>();
				for(Face face : faceList) {
					photoList.add(face.getPhoto());
				}
				return photoList;
			}
		}
		return null;
	}

	@Override
	public boolean deletePhoto(String stuId, String photo) {
		if (photo != null && stuId != null) {
			Face record = faceMapper.selectByPrimaryKey(photo);
			if (record != null) {
				String faceId = record.getFaceId();
				boolean removeFaceResult = true;
				if (faceId != null && !faceId.trim().equals("")) {
					 removeFaceResult = FaceUtil.removeFaceFromPerson(stuId, faceId);
				}
				if (removeFaceResult) {
					faceMapper.deleteByPrimaryKey(photo);
					File originFile = new File(Constant.IMAGE_PATH + stuId + "/" + photo);
					File compressFile = new File(Constant.COMPRESS_IMAGE_PATH + stuId + "/" + photo);
					FileUtil.deleteDir(originFile);
					FileUtil.deleteDir(compressFile);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean deleteMultiPhoto(String stuId, List<String> photoList) {
		if (stuId != null && photoList != null) {
			for(String photo : photoList) {
				deletePhoto(stuId, photo);
			}
			return true;
		}
		return false;
	}


}
