package com.liujan.service.impl;

import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.Face;
import com.liujan.entity.FaceExample;
import com.liujan.mapper.FaceMapper;
import com.liujan.service.FaceService;
import com.liujan.util.FaceUtil;
import com.liujan.util.FileUtil;
import com.liujan.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("faceService")
public class FaceServiceImpl implements FaceService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private FaceMapper faceMapper;
	private FaceExample faceExample = new FaceExample();

	@Override
	public List<Face> listAllFace() {
		faceExample.clear();
		return faceMapper.selectByExample(faceExample);
	}

	@Override
    @Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
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
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public Result<String> addPhotoByStuId(String stuId, MultipartFile file) {
		Result<String> result = new Result<String>();
		try {
			int r = (int) (Math.random() * 1000);
			String originalName = file.getOriginalFilename();
			String suffix = originalName.substring(originalName.lastIndexOf(".")); //文件后缀名
            String fileName = new Date().getTime() + "_" + r + "_" + stuId + suffix;
			if (fileName == null || fileName.equals("")) {
				return result.status(Result.Status.ERROR).data(originalName);
			}
			String path = Constant.IMAGE_PATH + stuId + "/" + fileName;
			//判断图片文件夹和压缩图片的文件夹是否存在，不存在则创建
			File isImageExists = new File(Constant.IMAGE_PATH + stuId + "/");
			if (!isImageExists.isDirectory()) {
				isImageExists.mkdirs();
			}
			File isCompressExists = new File(Constant.COMPRESS_IMAGE_PATH + stuId + "/");
			if (!isCompressExists.exists()) {
				isCompressExists.mkdirs();
			}
            //把图片写入到本地磁盘
			File localFile = new File(path);
            file.transferTo(localFile);
            //压缩图片并写入磁盘
            String compressName = Constant.COMPRESS_IMAGE_PATH + stuId + "/" + fileName;
            ImageUtil.compressImage(Constant.IMAGE_PATH + stuId + "/" + fileName, compressName);

            Face record = new Face();
            record.setStuId(stuId);
            record.setPhoto(fileName);
            int count = faceMapper.insertSelective(record);
            if (count > 0)
                return result.status(Result.Status.SUCCESS).data(file.getOriginalFilename());
            else
                return result.status(Result.Status.ERROR);
		}
		catch (Exception e) {
			logger.error("add photo by stuId error!", e);
			return result.status(Result.Status.ERROR).data(file.getOriginalFilename());
		}
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
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public Result<Void> deletePhoto(String stuId, String photo) {
        Result<Void> result = new Result<Void>();
        try {
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
                        return result.status(Result.Status.SUCCESS);
                    }
                }
            }
            return result.status(Result.Status.ERROR);
        }
        catch (Exception e){
            logger.error("delete photo error!", e);
            return result.status(Result.Status.ERROR);
        }
	}

	@Override
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public Result<Void> deleteMultiPhoto(String stuId, List<String> photoList) {
		Result<Void> result = new Result<Void>();
        try {
            if (stuId != null && photoList != null) {
                for(String photo : photoList) {
                    deletePhoto(stuId, photo);
                }
                return result.status(Result.Status.SUCCESS);
            }
            else
                return result.status(Result.Status.ERROR);
        }
        catch (Exception e) {
            logger.error("delete multi photo error!", e);
            return result.status(Result.Status.ERROR);
        }

	}


}
