package com.liujan.controller;

import com.alibaba.fastjson.JSON;
import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.Student;
import com.liujan.entity.Teacher;
import com.liujan.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @RequestMapping(value = {"index.html", "signin.html"}, method = RequestMethod.GET)//签到页面
    public ModelAndView index(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"login.html"}, method = RequestMethod.GET) //显示登陆界面
    public ModelAndView login(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"login.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam("stuId") String stuId,
                        @RequestParam("pwd") String pwd,
                        HttpServletRequest request) {
        Result<Void> result = studentService.login(stuId, pwd);
        int status = result.getStatus();
        if (status == Result.Status.SUCCESS.getStatus()) {
            request.getSession().setAttribute("stuId", stuId);
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"logout.html"}, method = RequestMethod.GET)
    public ModelAndView logout(ModelAndView modelAndView, HttpServletRequest request) {
        request.getSession().removeAttribute("stuId");
        request.getSession().removeAttribute("name");
        modelAndView.setViewName("redirect:/student/login.html");
        return modelAndView;
    }


    //注册页面
    @RequestMapping(value = {"register.html"}, method = RequestMethod.GET)
    public ModelAndView register(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"register.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String register(Student student, HttpServletRequest request) {
        Result<Void> result = studentService.register(student, request);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"siginin.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String siginIn(@RequestParam("stuId") String stuId,
                          @RequestParam("pwd") String pwd,
                          HttpServletRequest request) {
        Result<Void> result = studentService.siginIn(stuId, pwd, request);
        return JSON.toJSONString(result);
    }

    @RequestMapping("success.html")
    public ModelAndView SiginInSuccess(ModelAndView modelAndView, HttpServletRequest request) {
        String stuName = (String) request.getSession().getAttribute("name");
        try {
            stuName = new String(java.net.URLDecoder.decode(stuName, "UTF-8"));
            String stuId = (String) request.getSession().getAttribute("stuId");
            int courseId = (Integer) request.getSession().getAttribute("courseId");
            String courseName = courseService.getCourseById(courseId).getCourseName();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:EEEE").format(new Date());

            modelAndView.addObject("studentName", stuName);
            modelAndView.addObject("stuId", stuId);
            modelAndView.addObject("courseName", courseName);
            modelAndView.addObject("currentTime", currentTime);

            return modelAndView;
        } catch (UnsupportedEncodingException e) {
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

    @RequestMapping(value = {"search.html"}, method = RequestMethod.GET)
    public ModelAndView search(@RequestParam("courseId")int courseId, ModelAndView modelAndView, HttpServletRequest request) {
        Course course = courseService.getCourseById(courseId);
        String stuId = (String) request.getSession().getAttribute("stuId");
        List<Statistic> statisticList = statisticService.getSiginListByStuId(stuId);
        modelAndView.addObject("course", course);
        modelAndView.addObject("statisticList", statisticList);
        return modelAndView;
    }

    @RequestMapping(value = {"change_course.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String changeCourse(@RequestParam("teacherId") int teacherId) {
        Result<List<Course>> result = courseService.getCourseListByTeacherId(teacherId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"info/my_info.html"}, method = RequestMethod.GET)
    public ModelAndView showMyInfo(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        Student student = studentService.getStudentById(stuId);
        modelAndView.addObject("student", student);
        return modelAndView;
    }

    @RequestMapping(value = {"info/update_info.html"}, method = RequestMethod.GET)
    public ModelAndView updateInfo(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        Student student = studentService.getStudentById(stuId);
        modelAndView.addObject("student", student);
        return modelAndView;
    }

    @RequestMapping(value = {"info/update_info.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateInfo(Student student) {
        Result<Void> result = studentService.updateInfo(student);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"photo/upload_photo_page.html"}, method = RequestMethod.GET)
    public ModelAndView showUploadPhotoPage(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"photo/upload_photo.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String uploadPhoto(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        Result<String> result = faceService.addPhotoByStuId(stuId, file);
        return JSON.toJSONString(result);
    }

    @RequestMapping("photo/photo_list.html")
    public ModelAndView managePhoto(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        List<String> photoList = faceService.listPhotoByStuId(stuId);
        modelAndView.addObject("photoList", photoList);

        return modelAndView;
    }

    @RequestMapping("photo/getPhoto.html")
    public void getPhoto(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam("path")String path) {
        if (path == null) {
            return;
        }
        String stuId = (String) request.getSession().getAttribute("stuId");
        path = Constant.COMPRESS_IMAGE_PATH + stuId + "/" + path;
        File file = new File(path);
        response.setContentLength((int) file.length());
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            byte[] buf = new byte[1014];
            int count = 0;
            while ((count = inputStream.read(buf)) >= 0) {
                outputStream.write(buf, 0, count);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("get photo error!", e);
        }
    }

    @RequestMapping("photo/deletePhotoByName.html")
    @ResponseBody
    public String deletePhotoByName(@RequestParam("photo")String photo, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        Result<Void> result = faceService.deletePhoto(stuId, photo);
        return JSON.toJSONString(result);
    }

    @RequestMapping("photo/deleteMultiPhoto.html")
    @ResponseBody
    public String deleteMultiPhoto(@RequestParam("photo")String photo, HttpServletRequest request) {
        String stuId = (String) request.getSession().getAttribute("stuId");
        String[] photos = photo.split(";");

        List<String> photoList = new ArrayList<String>();
        for (int i = 0; i < photos.length; i++) {
            photoList.add(photos[i]);
        }

        Result<Void> result = faceService.deleteMultiPhoto(stuId, photoList);
        return JSON.toJSONString(result);
    }

}
