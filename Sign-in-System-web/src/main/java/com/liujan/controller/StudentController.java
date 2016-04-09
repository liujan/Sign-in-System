package com.liujan.controller;

import com.liujan.constant.Constant;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.Student;
import com.liujan.entity.Teacher;
import com.liujan.json.JsonData;
import com.liujan.service.*;
import com.liujan.util.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Controller
@RequestMapping("/student")
public class StudentController {
	private Logger logger = LoggerFactory.getLogger(StudentController.class);
	@Autowired
	private StudentService studentService;
	@Autowired
	private CourseService courseService;
	@Autowired
	private InfoService infoService;
	@Autowired
	private TeacherService teacherSerivce;
	@Autowired
	private StatisticService statisticService;
	@Autowired
	private FaceService faceService;
	
	@RequestMapping("index.html")//签到页面
	public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
		return modelAndView;
	}
	
	@RequestMapping("login.html") //显示登陆界面
	public ModelAndView showLoginPage(ModelAndView modelAndView, HttpServletRequest request) {
		return modelAndView;
	}
	
	@RequestMapping("login_verify.html")
	public @ResponseBody
	String login(ModelAndView modelAndView, HttpServletRequest request) {
		String stuId = request.getParameter("stuId");
		String pwd = request.getParameter("pwd");
		int result = studentService.login(stuId, pwd);
		JsonData jsonData = new JsonData();
		jsonData.setStatus(result);
		if (result != 1) {
			String message;
			try {
				message = new String(java.net.URLEncoder.encode("学号或密码错误", "UTF-8"));
				jsonData.setMessage(message);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
			
		}
		else{
			String name = studentService.getStudentById(stuId).getName();
			request.getSession().setAttribute("stuId", stuId);
			request.getSession().setAttribute("name", name);
		}
		return JSONObject.fromObject(jsonData).toString();
	}
	
	@RequestMapping("logout.html")
	public ModelAndView logout(ModelAndView modelAndView, HttpServletRequest request) {
		request.getSession().removeAttribute("stuId");
		request.getSession().removeAttribute("name");
		modelAndView.setViewName("redirect:/student/login.html");
		return modelAndView;
	}
	
	
	//注册页面
	@RequestMapping("register.html")
	public ModelAndView register(ModelAndView modelAndView, HttpServletRequest request) {
		return modelAndView;
	}
	
	@RequestMapping("register_save.html")
	public @ResponseBody
	String registerSave(ModelAndView modelAndView, HttpServletRequest request) {
		String osVersion = request.getHeader("User-Agent");
		int flag = 1;
		if (osVersion.contains("Macintosh") || osVersion.contains("macintosh")  || ((osVersion.contains("Windows")
				|| osVersion.contains("windows")) && (osVersion.contains("NT") || osVersion.contains("nt") || osVersion.contains("Nt"))) ) {
			flag = -1;
		}
		int result = 0;
		try {
			if (flag == 1) {
				request.setCharacterEncoding("UTF-8");
				String name = request.getParameter("name").trim();
				String stuId = request.getParameter("stuId").trim();
				String pwd = request.getParameter("pwd").trim();
				String email = request.getParameter("email");
				String macAddr = NetUtil.getMacAddress(request.getRemoteAddr());
				result = studentService.Register(stuId, name, macAddr, pwd, email);
			}
			else
				result = -2;
			
			String message = "";
			JsonData jsonData = new JsonData();
			if (result == 1 && flag == 1) {
				jsonData.setStatus(1);
				message = new String(java.net.URLEncoder.encode("注册成功","UTF-8"));
			}
			else{
				jsonData.setStatus(-1);
				if (result == -1) 
					message = new String(java.net.URLEncoder.encode("该学号已被注册","UTF-8"));
				else if (result == -2)
					message = new String(java.net.URLEncoder.encode("请用手机端进行注册","UTF-8"));
				else
					message = new String(java.net.URLEncoder.encode("发生未知错误","UTF-8"));
			}
			jsonData.setMessage(message);
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
		
	}
	
	@RequestMapping("/siginin.html")
	public @ResponseBody
	String SiginIn(ModelAndView modelAndView, HttpServletRequest request) {
		
		String pwd = request.getParameter("pwd").trim();
		String stuId = request.getParameter("stuId").trim();
		String osVersion = request.getHeader("User-Agent");
		int result = 0;
		if (osVersion.contains("Macintosh") || osVersion.contains("macintosh")  || ((osVersion.contains("Windows")
				|| osVersion.contains("windows")) && (osVersion.contains("NT") || osVersion.contains("nt") || osVersion.contains("Nt"))) ) {
			result = -2;
		}
		
		String ip = request.getRemoteAddr();
		String macAddr = NetUtil.getMacAddress(ip);
		int courseId = infoService.getCourseId();
		int week = infoService.getWeek();
		
		if (courseId == -1 || week == -1) {
			result = -5;
		}
		else if (macAddr == null || macAddr.isEmpty() || result != 0)
			result = -2;
		else {
			result = studentService.SiginIn(stuId, pwd, macAddr, courseId, week);
		}
		
		try {
			String message = "";
			JsonData jsonData = new JsonData();
			if (result == 1) {
				String name = studentService.getStudentById(stuId).getName();
				name = new String(java.net.URLEncoder.encode(name,"UTF-8"));
				message = new String(java.net.URLEncoder.encode("签到成功", "UTF-8"));
				request.getSession().setAttribute("stuId", stuId);
				request.getSession().setAttribute("courseId", courseId);
				request.getSession().setAttribute("name", name);
				request.getSession().setAttribute("week", week);
			}
			else {
				if (result == 0) 
					message = new String(java.net.URLEncoder.encode("发生未知错误", "UTF-8"));
				else if (result == -1) 
					message = new String(java.net.URLEncoder.encode("该学号为注册，请先注册", "UTF-8"));
				else if (result == -2)
					message = new String(java.net.URLEncoder.encode("请用本人的手机签到", "UTF-8"));
				else if (result == -3)
					message = new String(java.net.URLEncoder.encode("密码不对", "UTF-8"));
				else 
					message = new String(java.net.URLEncoder.encode("老师还没登陆，请稍后再签到", "UTF-8"));
			}
			
			jsonData.setStatus(result);
			jsonData.setMessage(message);
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
		
	}
	
	@RequestMapping("success.html")
	public ModelAndView SiginInSuccess(ModelAndView modelAndView, HttpServletRequest request) {
		String name = (String) request.getSession().getAttribute("name");
		try {
			name = new String(java.net.URLDecoder.decode(name, "UTF-8"));
			String stuId = (String) request.getSession().getAttribute("stuId");
			int courseId = (Integer) request.getSession().getAttribute("courseId");
			String courseName = courseService.getCourseById(courseId).getCourseName();
			String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:EEEE").format(new Date());
			
			modelAndView.addObject("studentName", name);
			modelAndView.addObject("stuId", stuId);
			modelAndView.addObject("courseName", courseName);
			modelAndView.addObject("currentTime", currentTime);
			
			return modelAndView;
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping("home.html")
	public ModelAndView home(ModelAndView modelAndView, HttpServletRequest request) {
		List<Teacher> teacherList = teacherSerivce.getTeacherList();
		List<Integer> teacherIdList = new ArrayList<Integer>();
		for (Teacher teacher : teacherList) {
			int id = teacher.getTeacherId();
			teacherIdList.add(id);
		}
		List<Course> courseList = courseService.getAllCourseList(teacherIdList);
		
		modelAndView.addObject("teacherList", teacherList);
		modelAndView.addObject("courseList", courseList);
		return modelAndView;
	}
	
	@RequestMapping("search.html")
	public ModelAndView search(ModelAndView modelAndView, HttpServletRequest request) {
		int courseId = Integer.parseInt(request.getParameter("course"));
		Course course = courseService.getCourseById(courseId);
		String stuId = (String) request.getSession().getAttribute("stuId");
		List<Statistic> statisticList = statisticService.getSiginListByStuId(stuId);
		modelAndView.addObject("course", course);
		modelAndView.addObject("statisticList", statisticList);
		return modelAndView;
	}
	
	@RequestMapping("change_course.html")
	public @ResponseBody
	String changeCourse(ModelAndView modelAndView, HttpServletRequest request) {
		int teacherId = Integer.parseInt(request.getParameter("teacherId"));
		List<Course> courseList = courseService.getCourseList(teacherId);
		try {
			for (int i = 0; i < courseList.size(); i++) {
				String courseName =  new String(java.net.URLEncoder.encode(courseList.get(i).getCourseName(),"UTF-8")); 
				courseList.get(i).setCourseName(courseName);
			}
			JsonData jsonData = new JsonData();
			jsonData.setData(courseList);
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
		
	}
	
	@RequestMapping("info/my_info.html")
	public ModelAndView showMyInfo(ModelAndView modelAndView, HttpServletRequest request) {
		String stuId = (String) request.getSession().getAttribute("stuId");
		Student student = studentService.getStudentById(stuId);
		modelAndView.addObject("student", student);
		return modelAndView;
	}
	
	@RequestMapping("info/update_info.html")
	public ModelAndView updateInfo(ModelAndView modelAndView, HttpServletRequest request) {
		String stuId = (String) request.getSession().getAttribute("stuId");
		Student student = studentService.getStudentById(stuId);
		modelAndView.addObject("student", student);
		return modelAndView;
	}
	
	@RequestMapping("info/update_save.html")
	public @ResponseBody
	String updateSave(ModelAndView modelAndView, HttpServletRequest request) {
		String name = request.getParameter("name").trim();
		String email = request.getParameter("email").trim();
		String userPwd = request.getParameter("userPwd");
		String stuId = (String) request.getSession().getAttribute("stuId");
		int result = studentService.updateInfo(stuId, name, email, userPwd);
		JsonData jsonData = new JsonData();
		
		try {
			if (result == 1)
				jsonData.setMessage(new String(java.net.URLEncoder.encode("修改成功", "UTF-8")));
			else
				jsonData.setMessage(new String(java.net.URLEncoder.encode("修改失败", "UTF-8")));
			jsonData.setStatus(result);
			
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping("photo/upload_photo_page.html")
	public ModelAndView showUploadPhotoPage(ModelAndView modelAndView, HttpServletRequest request) {
		return modelAndView;
	}
	
	@RequestMapping("photo/upload_photo.html")
	public @ResponseBody
	String uploadPhoto(ModelAndView modelAndView, HttpServletRequest request) {
		String fileName = "";
		boolean uploadResult = false;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());

		// 先判断request中是否包涵multipart类型的数据，
		if (multipartResolver.isMultipart(request)) {
			// 再将request中的数据转化成multipart类型的数据
			String stuId = (String) request.getSession().getAttribute("stuId");
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> iter = multiRequest.getFileNames();
			while (iter.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iter.next());
				if (file != null) {
					Date date = new Date();
					int r = (int) (Math.random() * 1000);
					String originalName = file.getOriginalFilename();
					String suffix = originalName.substring(originalName.lastIndexOf(".")); //文件后缀名
					fileName = date.getTime() + "_" + r + "_"  + stuId+ suffix;
					if (fileName == null || fileName.equals("")) {
						continue;
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
					File localFile = new File(path);
					
					try {
						// 写文件到本地		
						boolean result = faceService.addPhotoByStuId(stuId, fileName);
						if (result) {
							file.transferTo(localFile);	
							String compressName = Constant.COMPRESS_IMAGE_PATH + stuId + "/" + fileName;
							ImageUtil.compressImage(Constant.IMAGE_PATH + stuId + "/" + fileName, compressName);
							uploadResult = true;
						}
						fileName = file.getOriginalFilename();
						
					}  catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}
		}
		
		try {
			JsonData jsonData = new JsonData();
			if (uploadResult) {
				jsonData.setStatus(1);
				jsonData.setMessage(java.net.URLEncoder.encode(fileName + "上传成功", "UTF-8"));
			}
			else {
				jsonData.setStatus(0);
				jsonData.setMessage(java.net.URLEncoder.encode(fileName + "上传失败", "UTF-8"));
			}
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping("photo/photo_list.html")
	public ModelAndView managePhoto(ModelAndView modelAndView, HttpServletRequest request) {
		String stuId = (String) request.getSession().getAttribute("stuId");
		List<String> photoList = faceService.listPhotoByStuId(stuId);
		modelAndView.addObject("photoList", photoList);
		
		return modelAndView;
	}
	@RequestMapping("photo/getPhoto.html")
	public void getPhoto(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getParameter("path");
		if (path == null) {
			return;
		}
		String stuId = (String) request.getSession().getAttribute("stuId");
		path = Constant.COMPRESS_IMAGE_PATH + stuId + "/" + path; 
		File file = new File(path);
		response.setContentLength((int)file.length());
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			OutputStream outputStream = response.getOutputStream();
			byte[] buf = new byte[1014];
			int count = 0;
			while((count = inputStream.read(buf)) >= 0) {
				outputStream.write(buf, 0, count);
			}
			outputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
	}
	
	@RequestMapping("photo/deletePhotoByName.html")
	public @ResponseBody
	String deletePhotoByName(ModelAndView modelAndView, HttpServletRequest request) {
		String photo = request.getParameter("photo");
		if (photo == null || photo.trim().equals("")) {
			return null;
		}
		String stuId = (String) request.getSession().getAttribute("stuId");
		boolean flag = faceService.deletePhoto(stuId, photo);
		
		try {
			JsonData jsonData = new JsonData();
			String message;
			if (flag) {
				jsonData.setStatus(1);
				message = new String(java.net.URLEncoder.encode("删除成功", "UTF-8"));
			}
			else {
				jsonData.setStatus(0);
				message = new String(java.net.URLEncoder.encode("删除失败", "UTF-8"));
			}
			jsonData.setMessage(message);
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping("photo/deleteMultiPhoto.html")
	public @ResponseBody
	String deleteMultiPhoto(ModelAndView modelAndView, HttpServletRequest request) {
		String stuId = (String) request.getSession().getAttribute("stuId");
		String[] photos = request.getParameter("photo").split(";");
		List<String> photoList = new ArrayList<String>();
		for (int i = 0; i < photos.length; i++) {
			photoList.add(photos[i]);
		}
		
		boolean flag = faceService.deleteMultiPhoto(stuId, photoList);
		
		try {
			JsonData jsonData = new JsonData();
			String message;
			
			if (flag) {
				message = new String(java.net.URLEncoder.encode("删除成功", "UTF-8"));
				jsonData.setStatus(1);
			}
			else {
				message = new String(java.net.URLEncoder.encode("删除失败", "UTF-8"));
				jsonData.setStatus(0);
			}
			jsonData.setMessage(message);
			return JSONObject.fromObject(jsonData).toString();
		}
		catch(UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
}
