package com.liujan.util;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.liujan.constant.Constant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceUtil {
	private static final HttpRequests requests = new HttpRequests(Constant.API_KEY, Constant.API_SECRET, true, true);

	public static Map<String, String> addPhotoToGroup(List<String> fileNameList, String stuId)  {
		if (fileNameList == null || fileNameList.isEmpty())
			return null;
		
		try {
			Map<String, String> photoIdMap = new HashMap<String, String>();
			//判断group是否存在，否则创建group
			org.json.JSONObject groupList = requests.infoGetGroupList();
			boolean hasGroup = false;
			for (int i = 0; i < groupList.getJSONArray("group").length(); i++) {
				String groupName = groupList.getJSONArray("group").getJSONObject(i).getString("group_name");
				if (groupName.equals(Constant.GROUP_NAME)) {
					hasGroup = true;
					break;
				}
			}
			if (!hasGroup) {
				requests.groupCreate(new PostParameters().setGroupName(Constant.GROUP_NAME));
			}
			
			// 判断该人物(person_name为学号是否存在，否则创建该人物)
			boolean hasPerson = false;
			org.json.JSONObject personList = requests
					.groupGetInfo(new PostParameters().setGroupName(Constant.GROUP_NAME));
			for (int i = 0; i < personList.getJSONArray("person").length(); i++) {
				String personName = personList.getJSONArray("person").getJSONObject(i).getString("person_name");
				if (personName.equals(stuId)) {
					hasPerson = true;
					break;
				}
			}
			if (!hasPerson) {
				requests.personCreate(new PostParameters().setPersonName(stuId).setGroupName(Constant.GROUP_NAME));
			}
			
			
			for (String fileName : fileNameList) {
				String id = new String();
				String source = Constant.COMPRESS_IMAGE_PATH + stuId + "/" + fileName;

				// 为该人物添加face
				org.json.JSONObject result = requests.detectionDetect(new PostParameters().setImg(new File(source)));
				for (int i = 0; i < result.getJSONArray("face").length(); i++) {
					String faceId = result.getJSONArray("face").getJSONObject(i).getString("face_id");
					requests.personAddFace(new PostParameters().setPersonName(stuId).setFaceId(faceId));
					id = faceId;
				}
				photoIdMap.put(fileName, id);
			}
			// 重新训练group
			JSONObject identifyResult = requests.trainIdentify(new PostParameters().setGroupName(Constant.GROUP_NAME));
			String identifySessionId = identifyResult.getString("session_id");
			while(true) {
				JSONObject infoResult = requests.infoGetSession(new PostParameters().setSessionId(identifySessionId));
				String isSuccess = infoResult.getString("status");
				if (isSuccess.equals("SUCC")) {
					break;
				}
				else if (isSuccess.equals("FAILED")) {
					return null;
				}
			}
			return photoIdMap;

		}
		catch(FaceppParseException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}

	public static boolean removeFaceFromPerson(String personName, String faceId) {
		try {
			JSONObject result = requests
					.personRemoveFace(new PostParameters().setPersonName(personName).setFaceId(faceId));
			if (result.getBoolean("success")) {
				return true;
			} else {
				return false;
			}
		} catch (FaceppParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static Map<String, Double> recognition(String imageName, String stuId) {
		try {
			Map<String, Double> personNameConfidence = new HashMap<String, Double>();
			String source = Constant.COMPRESS_IMAGE_PATH + stuId + imageName;
			ImageUtil.compressImage(Constant.IMAGE_PATH + stuId + imageName, source);
			HttpRequests requests = new HttpRequests(Constant.API_KEY, Constant.API_SECRET, true, true);
			org.json.JSONObject result = requests.detectionDetect(new PostParameters().setImg(new File(source)));
			JSONArray faceList = result.getJSONArray("face");
			for (int i = 0; i < faceList.length(); i++) {
				String faceId = faceList.getJSONObject(i).getString("face_id");
				org.json.JSONObject identifyResult = requests
						.recognitionIdentify(new PostParameters().setGroupName(Constant.GROUP_NAME).setKeyFaceId(faceId));
				String personName = identifyResult.getJSONArray("face").getJSONObject(0).getJSONArray("candidate")
						.getJSONObject(0).getString("person_name");
				double confidence = identifyResult.getJSONArray("face").getJSONObject(0).getJSONArray("candidate")
						.getJSONObject(0).getDouble("confidence");
				if (confidence < Constant.CONFIDENCE_THRESHOLD) {
					continue;
				}
				if (personNameConfidence.containsKey(personName) && personNameConfidence.get(personName) >= confidence) {
					continue;
				}
				personNameConfidence.put(personName, confidence);
			}

			return personNameConfidence;
		}
		catch(FaceppParseException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static boolean removePerson(String stuId) {
		try {
			requests.groupRemovePerson(new PostParameters().setPersonName(stuId).setGroupName(Constant.GROUP_NAME));
		} catch (FaceppParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				requests.personDelete(new PostParameters().setPersonName(stuId));
			} catch (FaceppParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
}
