package com.liujan.service;

import com.liujan.entity.Face;

import java.util.List;

public interface FaceService {
	public List<Face> listAllFace();
	public List<Face> listEmptyFace();
	public boolean updateFaceId(String photo, String faceId);
	public boolean addPhotoByStuId(String stuId, String photo);
	public List<String> listPhotoByStuId(String stuId);
	public boolean deletePhoto(String stuId, String photo);
	public boolean deleteMultiPhoto(String stuId, List<String> photoList);
}
