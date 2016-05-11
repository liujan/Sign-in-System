package com.liujan.service;

import com.liujan.domain.Result;
import com.liujan.entity.Face;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FaceService {
	public List<Face> listAllFace();
	public List<Face> listEmptyFace();
	public boolean updateFaceId(String photo, String faceId);
	public Result<String> addPhotoByStuId(String stuId, MultipartFile file);
	public List<String> listPhotoByStuId(String stuId);
	public Result<Void> deletePhoto(String stuId, String photo);
	public Result<Void> deleteMultiPhoto(String stuId, List<String> photoList);
}
