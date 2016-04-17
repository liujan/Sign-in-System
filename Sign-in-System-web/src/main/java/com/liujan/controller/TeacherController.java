package com.liujan.controller;

import com.liujan.constant.Constant;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.Student;
import com.liujan.entity.Teacher;
import com.liujan.json.JsonData;
import com.liujan.service.*;
import com.liujan.util.FaceUtil;
import com.liujan.util.MailSender;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("teacher")
public class TeacherController {
    private Logger logger = LoggerFactory.getLogger(TeacherController.class);
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private StudentService studentSerivce;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private MailSender mailSender;

    @RequestMapping("home.html")
    public ModelAndView home(ModelAndView modelAndView, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if (teacherId != null) {
            List<Course> courseList = courseService.getCourseList(teacherId);
            modelAndView.addObject("courseList", courseList);
        } else {
            modelAndView.setViewName("redirect:/teacher/login.html");
        }
        return modelAndView;
    }

    @RequestMapping("index.html")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.setViewName("redirect:/teacher/login.html");
        return modelAndView;
    }

    @RequestMapping("set_course.html")
    public
    @ResponseBody
    String setCourse(ModelAndView modelAndView, HttpServletRequest request) {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int week = Integer.parseInt(request.getParameter("week"));
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        int result = infoService.setCourseId(courseId, week, teacherId);

        try {
            JsonData jsonData = new JsonData();
            jsonData.setStatus(result);
            String message = null;
            if (result == 1) {
                message = new String(java.net.URLEncoder.encode("设置成功", "UTF-8"));
                request.getSession().setAttribute("courseId", courseId);
                request.getSession().setAttribute("week", week);
            } else
                message = new String(java.net.URLEncoder.encode("设置失败", "UTF-8"));
            jsonData.setMessage(message);
            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping("login.html") // 显示登陆界面
    public ModelAndView showLoginPage(ModelAndView modelAndView, HttpServletRequest request) {
        return modelAndView;
    }

    @RequestMapping("login_verify.html")
    public
    @ResponseBody
    String login(ModelAndView modelAndView, HttpServletRequest request) {

        String name = request.getParameter("name");
        String pwd = request.getParameter("pwd");
        int result = teacherService.login(name, pwd);

        try {
            JsonData jsonData = new JsonData();
            jsonData.setStatus(result);
            String message;

            if (result != -1) {
                message = new String(java.net.URLEncoder.encode("登陆成功", "UTF-8"));
                request.getSession().setAttribute("teacherId", result);
                request.getSession().setAttribute("userName", name);
            } else
                message = new String(java.net.URLEncoder.encode("用户名或密码错误", "UTF-8"));
            jsonData.setMessage(message);

            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    @RequestMapping("logout.html")
    public ModelAndView logout(ModelAndView modelAndView, HttpServletRequest request) {
        request.getSession().removeAttribute("userName");
        request.getSession().removeAttribute("teacherId");
        modelAndView.setViewName("redirect:/teacher/login.html");
        return modelAndView;
    }

    @RequestMapping("register.html")
    public ModelAndView register(ModelAndView modelAndView, HttpServletRequest request) {
        return modelAndView;
    }

    @RequestMapping("regisger_save.html")
    public
    @ResponseBody
    String registerSave(ModelAndView modelAndView, HttpServletRequest request) {
        String name = request.getParameter("name");
        String teacherName = request.getParameter("teacher_name");
        String email = request.getParameter("email");
        String pwd = request.getParameter("pwd");

        int result = teacherService.register(name, teacherName, email, pwd);

        try {
            JsonData jsonData = new JsonData();
            String message;
            if (result == -1)
                message = new String(java.net.URLEncoder.encode("该邮箱已被注册", "UTF-8"));
            else if (result == -2)
                message = new String(java.net.URLEncoder.encode("该用户名已被注册", "UTF-8"));
            else if (result == 1)
                message = new String(java.net.URLEncoder.encode("注册成功", "UTF-8"));
            else {
                message = new String(java.net.URLEncoder.encode("注册失败", "UTF-8"));
            }
            jsonData.setStatus(result);
            jsonData.setMessage(message);

            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping("student/student_list.html")
    public ModelAndView listStudent(ModelAndView modelAndView, HttpServletRequest request) {
        List<Student> studentList = studentSerivce.listStudents();
        modelAndView.addObject("studentList", studentList);
        return modelAndView;
    }

    @RequestMapping("student/deleteStudentById.html")
    public
    @ResponseBody
    String deleteStudentById(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = request.getParameter("stuId");
        boolean result = studentSerivce.deleteStudentById(stuId);

        try {
            JsonData jsonData = new JsonData();

            if (result) {
                jsonData.setStatus(1);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("删除成功", "UTF-8")));
            } else {
                jsonData.setStatus(0);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("删除失败", "UTF-8")));
            }

            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping("student/change_phone_page.html")
    public ModelAndView changePhonePage(ModelAndView modelAndView, HttpServletRequest request) {
        return modelAndView;
    }

    @RequestMapping("student/change_phone.html")
    public
    @ResponseBody
    String changePhone(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = request.getParameter("stuId");
        int result = studentSerivce.changePhone(stuId);

        try {
            JsonData jsonData = new JsonData();
            String message = "";

            if (result == 1) {
                message = new String(java.net.URLEncoder.encode("换机成功", "UTF-8"));
            } else if (result == -1) {
                message = new String(java.net.URLEncoder.encode("该学生未注册，请直接注册", "UTF-8"));
            } else {
                message = new String(java.net.URLEncoder.encode("发生未知错误，换机失败", "UTF-8"));
            }
            jsonData.setMessage(message);
            jsonData.setStatus(result);

            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping("student/sendmail.html")
    public
    @ResponseBody
    String sendMail(ModelAndView modelAndView, HttpServletRequest request) {
        String receiver = request.getParameter("receiver");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");
        try {
            JsonData jsonData = new JsonData();
            if (!receiver.isEmpty()) {
                mailSender.send(receiver, subject, content);
                jsonData.setStatus(1);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("发送成功", "UTF-8")));
            } else {
                jsonData.setStatus(-1);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("发送失败", "UTF-8")));
            }
            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e3) {
            logger.error(e3.getMessage());
            return null;
        }
    }

    @RequestMapping("info/my_info.html")
    public ModelAndView showMyInfo(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Teacher teacher = teacherService.getMyInfo(teacherId);
        teacher.setTeacherId(0);
        modelAndView.addObject("teacher", teacher);
        return modelAndView;
    }

    @RequestMapping("info/update_info.html")
    public ModelAndView updateInfoPage(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Teacher teacher = teacherService.getMyInfo(teacherId);
        teacher.setTeacherId(0);
        modelAndView.addObject("teacher", teacher);
        return modelAndView;
    }

    @RequestMapping("info/update_save.html")
    public
    @ResponseBody
    String updateSave(ModelAndView modelAndView, HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String teacherName = request.getParameter("teacherName");
        String email = request.getParameter("email");
        String userPwd = request.getParameter("userPwd");
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        int result = teacherService.updateInfo(teacherId, userName, teacherName, email, userPwd);

        try {
            JsonData jsonData = new JsonData();
            jsonData.setStatus(result);
            String message = "";
            if (result != -1) {
                request.getSession().setAttribute("userName", userName);
                message = new String(java.net.URLEncoder.encode("修改成功", "UTF-8"));
            } else
                message = new String(java.net.URLEncoder.encode("发生未知错误，修改失败", "UTF-8"));
            jsonData.setMessage(message);
            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @RequestMapping("sigin/sigined_list.html") // 列出本周已签到的学生
    public ModelAndView siginedList(ModelAndView modelAndView, HttpServletRequest request) {

        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if (request.getSession().getAttribute("week") == null) {
            modelAndView.addObject("weekSet", -2);
        }
        int week = (Integer) request.getSession().getAttribute("week");
        List<Course> courseList = courseService.getCourseList(teacherId);
        if (courseList == null || courseList.isEmpty() || request.getSession().getAttribute("week") == null) {
            return modelAndView;
        }
        int courseId = courseList.get(0).getCourseId();
        if (courseList.get(0).getStudentList() == null ||
                courseList.get(0).getStudentList().trim().length() == 0) {
            modelAndView.addObject("importStudent", -2);
            modelAndView.addObject("week", week);
            modelAndView.addObject("courseList", courseList);
            return modelAndView;
        }
        List<Statistic> statisticList = statisticService.getSiginList(courseId, week);

        String courseName = courseService.getCourseById(courseId).getCourseName();
        modelAndView.addObject("courseName", courseName);
        modelAndView.addObject("week", week);
        modelAndView.addObject("courseList", courseList);
        modelAndView.addObject("courseId", courseId);
        modelAndView.addObject("statisticList", statisticList);

        return modelAndView;
    }

    @RequestMapping(value = "sigin/getStatisicByCourse.html", method = RequestMethod.POST)
    @ResponseBody
    public String getStatisicByCourse(HttpServletRequest request,
                                      @RequestParam("courseId") int courseId,
                                      @RequestParam("week") int week) {
        Course course = courseService.getCourseById(courseId);
        JsonData jsonData = new JsonData();
        if (course.getStudentList() == null ||
                course.getStudentList().trim().length() == 0) {
            jsonData.setStatus(-2);
            try {
                jsonData.setMessage(new String(java.net.URLEncoder.encode("本课程未导入学生", "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            jsonData.setData(new ArrayList<String>());
            return JSONObject.fromObject(jsonData).toString();
        }
        List<Statistic> statisticList = statisticService.getSiginList(courseId, week);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 0; i < statisticList.size(); i++) {
            Date d = statisticList.get(i).getSiginTime();
            String newD = simpleDateFormat.format(d);
            String stuId = statisticList.get(i).getStuId();
            statisticList.get(i).setStuId(stuId + "_" + newD);

            try {
                String stuName = new String(java.net.URLEncoder.encode(statisticList.get(i).getStuName(), "UTF-8"));
                statisticList.get(i).setStuName(stuName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        jsonData.setStatus(1);
        jsonData.setData(statisticList);
        return JSONObject.fromObject(jsonData).toString();
    }

    @RequestMapping(value = "sigin/unsigin_list.html")
    public ModelAndView unSiginList(HttpServletRequest request, ModelAndView modelAndView) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if ( request.getSession().getAttribute("week") == null) {
            modelAndView.addObject("weekSet", -2);
            return modelAndView;
        }
        int week = (Integer) request.getSession().getAttribute("week");
        List<Course> courseList = courseService.getCourseList(teacherId);
        if (courseList == null || courseList.isEmpty() || request.getSession().getAttribute("week") == null
                || courseList.get(0).getStudentList() == null
                || courseList.get(0).getStudentList().trim().length() == 0) {
            modelAndView.addObject("importStudent", -2);
            modelAndView.addObject("courseList", courseList);
            modelAndView.addObject("week", week);
            return modelAndView;
        }
        int courseId = courseList.get(0).getCourseId();

        List<String> stuIdList = Arrays.asList(courseList.get(0).getStudentList().split(Constant.courseStuIdSeperator));
        List<String> unSiginStuIdList = statisticService.getUnSiginStuIdList(courseId, week, stuIdList);
        modelAndView.addObject("stuIdList", unSiginStuIdList);
        modelAndView.addObject("courseList", courseList);
        modelAndView.addObject("week", week);
        modelAndView.addObject("weekSet", 1);
        return modelAndView;
    }
    @RequestMapping(value = "sigin/getUnSiginStatisicByCourse.html", method = RequestMethod.POST)
    @ResponseBody
    public String getUnSiginStatisicByCourse(HttpServletRequest request,
                                             @RequestParam("courseId") int courseId,
                                             @RequestParam("week") int week) {
        Course course = courseService.getCourseById(courseId);
        JsonData jsonData = new JsonData();
        if (course.getStudentList() == null || course.getStudentList().trim().length() == 0) {
            jsonData.setStatus(-2); //未导入学生
            try {
                jsonData.setMessage(new String(java.net.URLEncoder.encode("本课程未导入学生", "UTF-8")));
                jsonData.setData(new ArrayList<String>());
            } catch (UnsupportedEncodingException e) {
               logger.error(e.getMessage());
            }
            return JSONObject.fromObject(jsonData).toString();
        }
        List<String> stuIdList = Arrays.asList(course.getStudentList().split(Constant.courseStuIdSeperator));
        List<String> unSiginStuList = statisticService.getUnSiginStuIdList(courseId, week, stuIdList);
        jsonData.setStatus(1);
        jsonData.setData(unSiginStuList);
        return JSONObject.fromObject(jsonData).toString();
    }
    @RequestMapping("sigin/search_all.html")
    public ModelAndView searchAllPage(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseList(teacherId);
        List<Student> studentList = studentSerivce.listStudents();
        modelAndView.addObject("courseList", courseList);
        modelAndView.addObject("studengList", studentList);
        return modelAndView;
    }

    @RequestMapping("sigin/searchby_week.html")
    public ModelAndView searchByWeek(ModelAndView modelAndView, HttpServletRequest request) {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int week = Integer.parseInt(request.getParameter("week"));
        List<Statistic> statisticList = statisticService.getSiginList(courseId, week);
        List<Student> students = studentSerivce.listSiginedStudent(statisticList);
        String courseName = courseService.getCourseById(courseId).getCourseName();
        modelAndView.addObject("studentList", students);
        modelAndView.addObject("week", week);
        modelAndView.addObject("courseName", courseName);

        return modelAndView;
    }

    @RequestMapping("sigin/searchby_stuId.html")
    public ModelAndView searchByStuId(ModelAndView modelAndView, HttpServletRequest request) {
        String stuId = request.getParameter("stuId");
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseList(teacherId);

        List<Statistic> statisticList = statisticService.getSiginListByStuId(stuId);
        modelAndView.addObject("statisticList", statisticList);
        modelAndView.addObject("courseList", courseList);
        return modelAndView;
    }

    @RequestMapping("sigin/searchby_course.html")
    public ModelAndView searchByCourse(ModelAndView modelAndView, HttpServletRequest request) {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String courseName = courseService.getCourseById(courseId).getCourseName();
        Map<String, List<Integer>> studentMap = statisticService.getSiginListByCourse(courseId);
        modelAndView.addObject("statisticMap", studentMap);
        modelAndView.addObject("courseName", courseName);
        return modelAndView;
    }

    @RequestMapping("sigin/photo_sigin.html")
    public ModelAndView showPhotoSiginView(ModelAndView modelAndView, HttpServletRequest request) {
        return modelAndView;
    }

    @RequestMapping("sigin/upload_photo.html")
    public
    @ResponseBody
    String uploadPhoto(ModelAndView modelAndView, HttpServletRequest request) {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        JsonData jsonData = new JsonData();
        String fileName = new String();
        int courseId = -1;
        int week = -1;
        try {
            if (request.getSession().getAttribute("courseId") != null) {
                courseId = (Integer) request.getSession().getAttribute("courseId");
                week = (Integer) request.getSession().getAttribute("week");
            } else {
                jsonData.setStatus(-2);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("请先设置课程", "UTF-8")));
                return JSONObject.fromObject(jsonData).toString();
            }
            // 先判断request中是否包涵multipart类型的数据，
            if (multipartResolver.isMultipart(request)) {
                // 再将request中的数据转化成multipart类型的数据
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile(iter.next());
                    if (file != null) {
                        Date date = new Date();
                        int r = (int) (Math.random() * 1000);
                        String originalName = file.getOriginalFilename();
                        String suffix = originalName.substring(originalName.lastIndexOf(".")); //文件后缀名
                        fileName = date.getTime() + "_" + r + suffix;
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

                        Map<String, Double> siginResult = FaceUtil.recognition(fileName, Constant.TEACHER_IMAGE_PATH);
                        if (siginResult != null) {
                            statisticService.photoSigin(courseId, week, siginResult);
                        }
                        fileName = file.getOriginalFilename();

                    }
                }
            }

            jsonData.setStatus(1);
            jsonData.setMessage(new String(java.net.URLEncoder.encode(fileName + "上传成功", "UTF-8")));
            return JSONObject.fromObject(jsonData).toString();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        } catch (IllegalStateException e2) {
            logger.error(e2.getMessage());
            return null;
        }

    }


    @RequestMapping("course/courseList.html")
    public ModelAndView listAllCourse(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseList(teacherId);
        modelAndView.addObject("couorseList", courseList);
        return modelAndView;
    }

    @RequestMapping("course/deleteCourseById.html") // 删除一门课程
    public
    @ResponseBody
    String deleteCourseById(ModelAndView modelAndView, HttpServletRequest request) {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        int result = courseService.deleteCourseById(courseId, teacherId);

        try {
            JsonData jsonData = new JsonData();
            jsonData.setStatus(result);
            String message = null;
            if (result == 1)
                message = new String(java.net.URLEncoder.encode("删除成功", "UTF-8"));
            else
                message = new String(java.net.URLEncoder.encode("删除失败", "UTF-8"));
            jsonData.setMessage(message);
            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        }

    }

    @RequestMapping("course/add_course.html")
    public ModelAndView addCoursePage(ModelAndView modelAndView, HttpServletRequest request) {
        return modelAndView;
    }

    @RequestMapping("course/add_course_save.html")
    public
    @ResponseBody
    String addCourseSave(HttpServletRequest request,
                         @RequestParam("courseName") String courseName,
                         @RequestParam("beginTime") String beginTime,
                         @RequestParam("endTime") String endTime,
                         @RequestParam("dayInWeek") int dayInWeek,
                         @RequestParam("file") MultipartFile file) {
        beginTime += ":00";
        endTime += ":00";
        String name = file.getOriginalFilename();
        List<String> stuIdList = null;
        JsonData jsonData = new JsonData();
        //从文件中读取学号
        if (file != null && file.getOriginalFilename().endsWith(".csv")) {
            try {
                stuIdList = readFromCSV(file);
            } catch (IOException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            }
        } else if (file != null && file.getOriginalFilename().endsWith(".xlsx")) {
            try {
                stuIdList = readFromExcel(file);
            } catch (IOException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            } catch (InvalidFormatException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            }
        } else {
            jsonData.setStatus(0);
            try {
                jsonData.setMessage(new String(java.net.URLEncoder.encode("不支持的文件格式", "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            return JSONObject.fromObject(jsonData).toString();
        }

        int teacherId = (Integer) request.getSession().getAttribute("teacherId");

        try {
            int result = courseService.addCourse(courseName, beginTime, endTime, dayInWeek, teacherId, stuIdList);
            jsonData.setStatus(result);
            String message = "";
            if (result == 1)
                message = new String(java.net.URLEncoder.encode("添加成功", "UTF-8"));
            else
                message = new String(java.net.URLEncoder.encode("添加失败", "UTF-8"));
            jsonData.setMessage(message);

            return JSONObject.fromObject(jsonData).toString();
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            return null;
        } catch (ParseException e1) {
            logger.error(e1.getMessage());
            return null;
        }
    }

    @RequestMapping(value = "course/getstudentbycourseid.html", method = RequestMethod.POST)
    @ResponseBody
    public String getStudentByCourseId(@RequestParam("courseId") int courseId) {
        List<String> studentList = courseService.getStudentByCourseId(courseId);
        JsonData jsonData = new JsonData();
        jsonData.setStatus(1);
        jsonData.setData(studentList);
        return JSONObject.fromObject(jsonData).toString();
    }


    @RequestMapping(value = "course/deletecoursestudent.html", method = RequestMethod.POST)
    @ResponseBody
    public String deleteCourseStudent(@RequestParam("stuId") String stuId, @RequestParam("courseId") int courseId) {
        int result = courseService.deleteCourseStudent(stuId, courseId);
        JsonData jsonData = new JsonData();
        String msg = null;
        try {
            if (result == 1) {
                jsonData.setStatus(1);
                msg = new String(java.net.URLEncoder.encode("删除成功", "UTF-8"));
            } else {
                jsonData.setStatus(0);

                msg = new String(java.net.URLEncoder.encode("删除失败", "UTF-8"));

            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        jsonData.setMessage(msg);
        return JSONObject.fromObject(jsonData).toString();
    }

    @RequestMapping(value = "course/addstudenttocourse.html", method = RequestMethod.POST)
    @ResponseBody
    public String addStudentToCourse(HttpServletRequest request,
                                     @RequestParam("file") MultipartFile file,
                                     @RequestParam("courseId") int courseId) {
        List<String> stuIdList = null;
        JsonData jsonData = new JsonData();
        /* 从文件中读取学号 */
        if (file != null && file.getOriginalFilename().endsWith(".csv")) {
            try {
                stuIdList = readFromCSV(file);
            } catch (IOException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            }
        } else if (file != null && file.getOriginalFilename().endsWith(".xlsx")) {
            try {
                stuIdList = readFromExcel(file);
            } catch (IOException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            } catch (InvalidFormatException e) {
                logger.error(e.getMessage());
                jsonData.setStatus(0);
                try {
                    jsonData.setMessage(new String(java.net.URLEncoder.encode("添加失败", "UTF-8")));
                } catch (UnsupportedEncodingException e1) {
                    logger.error(e1.getMessage());
                }
                return JSONObject.fromObject(jsonData).toString();
            }
        } else {
            jsonData.setStatus(0);
            try {
                jsonData.setMessage(new String(java.net.URLEncoder.encode("不支持的文件格式", "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage());
            }
            return JSONObject.fromObject(jsonData).toString();
        }
        int result = courseService.addStudentToCourse(stuIdList, courseId);
        try {
            if (result > 0) {
                jsonData.setStatus(1);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("导入成功", "UTF-8")));

            } else {
                jsonData.setStatus(0);
                jsonData.setMessage(new String(java.net.URLEncoder.encode("导入失败", "UTF-8")));
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
        return JSONObject.fromObject(jsonData).toString();
    }

    private List<String> readFromCSV(MultipartFile file) throws IOException {
        List<String> lines = IOUtils.readLines(file.getInputStream(), String.valueOf(Charset.forName("UTF-8")));
        if (lines != null && lines.size() > Constant.IMPORT_MAX_SIZE) {
            throw new RuntimeException("导入列表最多不能超过:" + Constant.IMPORT_MAX_SIZE + "行");
        }
        List<String> stuIdList = new ArrayList<String>();
        for (String line : lines) {
            String[] cols = line.split(",");
            if (cols != null && cols.length >= 1 && StringUtils.isNotBlank(cols[0])) {
                stuIdList.add(cols[0]);
            }
        }
        return stuIdList;
    }


    private List<String> readFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        Workbook wb = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = null;
        int sum = 0;
        List<String> stuIdList = new ArrayList<String>();
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheet = wb.getSheetAt(i);
            sum += sheet.getLastRowNum();
        }
        if (sum > Constant.IMPORT_MAX_SIZE) {
            throw new RuntimeException("导入列表最多不能超过:" + Constant.IMPORT_MAX_SIZE + "行");
        }
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheet = wb.getSheetAt(i);
            for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                Cell cell = row.getCell(0);
                if (cell != null) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String val = cell.getStringCellValue();
                    if (val != null && StringUtils.isNotBlank(val)) {
                        stuIdList.add(val);
                    }
                }
            }
        }
        return stuIdList;
    }

}
