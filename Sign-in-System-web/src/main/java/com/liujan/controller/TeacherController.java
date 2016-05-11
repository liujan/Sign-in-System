package com.liujan.controller;

import com.alibaba.fastjson.JSON;
import com.liujan.constant.Constant;
import com.liujan.domain.Result;
import com.liujan.entity.Course;
import com.liujan.entity.Statistic;
import com.liujan.entity.Student;
import com.liujan.entity.Teacher;
import com.liujan.service.*;
import com.liujan.util.*;
import com.liujan.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = {"home.html"}, method = RequestMethod.GET)
    public ModelAndView home(ModelAndView modelAndView, HttpServletRequest request) {
        Integer teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if (teacherId != null) {
            Result<List<Course>> result = courseService.getCourseListByTeacherId(teacherId);
            modelAndView.addObject("courseList", result.getData());
        } else {
            modelAndView.setViewName("redirect:/teacher/login.html");
        }
        return modelAndView;
    }

    @RequestMapping(value = {"index.html"}, method = RequestMethod.GET)
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
        modelAndView.setViewName("redirect:/teacher/login.html");
        return modelAndView;
    }

    @RequestMapping(value = {"set_course.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String setCourse(@RequestParam("courseId") int courseId,
                            @RequestParam("week") int week,
                            HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Result<Void> result = infoService.setCourseId(courseId, week, teacherId);
        request.getSession().setAttribute("courseId", courseId);
        request.getSession().setAttribute("week", week);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"login.html"}, method = RequestMethod.GET) // 显示登陆界面
    public ModelAndView showLoginPage(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"login.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam("name") String name,
                        @RequestParam("pwd") String pwd, HttpServletRequest request) {
        Result<Integer> result = teacherService.login(name, pwd);
        if (result.getStatus() == Result.Status.SUCCESS.getStatus()) {
            request.getSession().setAttribute("teacherId", result.getData());
            request.getSession().setAttribute("userName", name);
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"logout.html"}, method = RequestMethod.GET)
    public ModelAndView logout(ModelAndView modelAndView, HttpServletRequest request) {
        request.getSession().removeAttribute("userName");
        request.getSession().removeAttribute("teacherId");
        modelAndView.setViewName("redirect:/teacher/login.html");
        return modelAndView;
    }

    @RequestMapping(value = {"register.html"}, method = RequestMethod.GET)
    public ModelAndView register(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"regisger_save.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String registerSave(Teacher teacher, HttpServletRequest request) {
        Result<Void> result = teacherService.register(teacher);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"student/student_list.html"}, method = RequestMethod.GET)
    public ModelAndView listStudent(ModelAndView modelAndView) {
        Result<List<Student>> result = studentSerivce.listStudents();
        modelAndView.addObject("studentList", result.getData());
        return modelAndView;
    }

    @RequestMapping(value = {"student/deleteStudentById.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String deleteStudentById(@RequestParam("stuId") String stuId) {
        Result<Void> result = studentSerivce.deleteStudentById(stuId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"student/change_phone_page.html"}, method = RequestMethod.GET)
    public ModelAndView changePhonePage(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"student/change_phone.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String changePhone(@RequestParam("stuId") String stuId) {
        Result<Void> result = studentSerivce.changePhone(stuId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"student/sendmail.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String sendMail(@RequestParam("receiver") String receiver,
                           @RequestParam("subject") String subject,
                           @RequestParam("content") String content) {
        Result<Void> result = new Result<Void>();
        if (!receiver.isEmpty()) {
            mailSender.send(receiver, subject, content);
            return JSON.toJSONString(result.status(Result.Status.SUCCESS));
        } else
            return JSON.toJSONString(result.status(Result.Status.ERROR));
    }

    @RequestMapping(value = {"info/my_info.html"}, method = RequestMethod.GET)
    public ModelAndView showMyInfo(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Result<Teacher> result = teacherService.getMyInfo(teacherId);
        modelAndView.addObject("teacher", result.getData());
        return modelAndView;
    }

    @RequestMapping(value = {"info/update_info.html"}, method = RequestMethod.GET)
    public ModelAndView updateInfoPage(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Result<Teacher> result = teacherService.getMyInfo(teacherId);
        modelAndView.addObject("teacher", result.getData());
        return modelAndView;
    }

    @RequestMapping(value = {"info/update_info.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String updateSave(Teacher teacher, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        teacher.setTeacherId(teacherId);
        Result<Void> result = teacherService.updateInfo(teacher);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"sigin/sigined_list.html"}, method = RequestMethod.GET) // 列出本周已签到的学生
    public ModelAndView siginedList(ModelAndView modelAndView, HttpServletRequest request) {
        if (request.getSession().getAttribute("teacherId") == null
            || request.getSession().getAttribute("week") == null) {
            modelAndView.addObject("weekSet", -2);
            return modelAndView;
        }
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        int week = (Integer) request.getSession().getAttribute("week");
        List<Course> courseList = courseService.getCourseListByTeacherId(teacherId).getData();
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
        List<Statistic> statisticList = statisticService.getSiginList(courseId, week).getData();

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
    public String getStatisicByCourse(@RequestParam("courseId") int courseId,
                                      @RequestParam("week") int week) {
        Result<List<Statistic>> result = statisticService.getSiginList(courseId, week);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "sigin/unsigin_list.html", method = RequestMethod.GET)
    public ModelAndView unSiginList(HttpServletRequest request, ModelAndView modelAndView) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        if (request.getSession().getAttribute("week") == null) {
            modelAndView.addObject("weekSet", -2);
            return modelAndView;
        }
        int week = (Integer) request.getSession().getAttribute("week");
        Result<List<Course>> courseResult = courseService.getCourseListByTeacherId(teacherId);
        if (courseResult.getData() == null || courseResult.getData().isEmpty() || request.getSession().getAttribute("week") == null
                || courseResult.getData().get(0).getStudentList() == null
                || courseResult.getData().get(0).getStudentList().trim().length() == 0) {
            modelAndView.addObject("importStudent", -2);
            modelAndView.addObject("courseList", courseResult.getData());
            modelAndView.addObject("week", week);
            return modelAndView;
        }
        int courseId = courseResult.getData().get(0).getCourseId();

        List<String> unSiginStuIdList = statisticService.getUnSiginStuIdList(courseId, week).getData();
        modelAndView.addObject("stuIdList", unSiginStuIdList);
        modelAndView.addObject("courseList", courseResult.getData());
        modelAndView.addObject("week", week);
        modelAndView.addObject("weekSet", 1);
        return modelAndView;
    }

    @RequestMapping(value = "sigin/getUnSiginStatisicByCourse.html", method = RequestMethod.POST)
    @ResponseBody
    public String getUnSiginStatisicByCourse(HttpServletRequest request,
                                             @RequestParam("courseId") int courseId,
                                             @RequestParam("week") int week) {
        Result<List<String>> result = statisticService.getUnSiginStuIdList(courseId, week);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"sigin/search_all.html"}, method = RequestMethod.GET)
    public ModelAndView searchAllPage(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseListByTeacherId(teacherId).getData();
        List<Student> studentList = studentSerivce.listStudents().getData();
        modelAndView.addObject("courseList", courseList);
        modelAndView.addObject("studengList", studentList);
        return modelAndView;
    }

    @RequestMapping(value = {"sigin/searchby_week.html"}, method = RequestMethod.GET)
    public ModelAndView searchByWeek(@RequestParam("courseId") int courseId,
                                     @RequestParam("week") int week,
                                     ModelAndView modelAndView) {
        List<Statistic> statisticList = statisticService.getSiginList(courseId, week).getData();
        List<Student> students = studentSerivce.listSiginedStudent(statisticList);
        String courseName = courseService.getCourseById(courseId).getCourseName();
        modelAndView.addObject("studentList", students);
        modelAndView.addObject("week", week);
        modelAndView.addObject("courseName", courseName);

        return modelAndView;
    }

    @RequestMapping(value = {"sigin/searchby_stuId.html"}, method = RequestMethod.GET)
    public ModelAndView searchByStuId(@RequestParam("stuId") String stuId,
                                      ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseListByTeacherId(teacherId).getData();

        List<Statistic> statisticList = statisticService.getSiginListByStuId(stuId);
        modelAndView.addObject("statisticList", statisticList);
        modelAndView.addObject("courseList", courseList);
        return modelAndView;
    }

    @RequestMapping(value = {"sigin/searchby_course.html"}, method = RequestMethod.GET)
    public ModelAndView searchByCourse(@RequestParam("courseId") int courseId,
                                       ModelAndView modelAndView) {
        String courseName = courseService.getCourseById(courseId).getCourseName();
        Map<String, List<Integer>> studentMap = statisticService.getSiginListByCourse(courseId);
        modelAndView.addObject("statisticMap", studentMap);
        modelAndView.addObject("courseName", courseName);
        return modelAndView;
    }

    @RequestMapping(value = {"sigin/photo_sigin.html"}, method = RequestMethod.GET)
    public ModelAndView showPhotoSiginView(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"sigin/upload_photo.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String uploadPhoto(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        int courseId = (Integer) request.getSession().getAttribute("courseId");
        int week = (Integer) request.getSession().getAttribute("week");
        Result<String> result = statisticService.photoSigin(courseId, week, file);
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = {"course/courseList.html"}, method = RequestMethod.GET)
    public ModelAndView listAllCourse(ModelAndView modelAndView, HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        List<Course> courseList = courseService.getCourseListByTeacherId(teacherId).getData();
        modelAndView.addObject("couorseList", courseList);
        return modelAndView;
    }

    @RequestMapping(value = {"course/deleteCourseById.html"}, method = RequestMethod.POST) // 删除一门课程
    @ResponseBody
    public String deleteCourseById(@RequestParam("courseId") int courseId,
                                   HttpServletRequest request) {
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        Result<Void> result = courseService.deleteCourseById(courseId, teacherId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"course/add_course.html"}, method = RequestMethod.GET)
    public ModelAndView addCoursePage(ModelAndView modelAndView) {
        return modelAndView;
    }

    @RequestMapping(value = {"course/add_course.html"}, method = RequestMethod.POST)
    @ResponseBody
    public String addCourseSave(@RequestParam("courseName")String courseName,
                                @RequestParam("beginTime")String beginTime,
                                @RequestParam("endTime")String endTime,
                                @RequestParam("dayInWeek")int dayInWeek,
                                @RequestParam("file") MultipartFile file,
                                HttpServletRequest request) {
        Result<Void> result = new Result<Void>();
        List<String> stuIdList = null;
        try {
            //从文件中读取学号
            if (file != null && file.getOriginalFilename().endsWith(".csv")) {
                stuIdList = readFromCSV(file);
            } else if (file != null && file.getOriginalFilename().endsWith(".xlsx")) {
                stuIdList = readFromExcel(file);
            } else {
                return JSON.toJSONString(result.status(Result.Status.ERROR));
            }
        } catch (Exception e) {
            logger.error("read stuId from file error!", e);
            return JSON.toJSONString(result.status(Result.Status.ERROR));
        }

        Course course = new Course();
        course.setCourseName(courseName);
        course.setBeginTime(DateUtil.parse(beginTime));
        course.setEndTime(DateUtil.parse(endTime));
        course.setDayInWeek(dayInWeek);
        int teacherId = (Integer) request.getSession().getAttribute("teacherId");
        course.setTeacherId(teacherId);
        result = courseService.addCourse(course, stuIdList);
        return JSON.toJSONString(result);

    }

    @RequestMapping(value = "course/getstudentbycourseid.html", method = RequestMethod.POST)
    @ResponseBody
    public String getStudentByCourseId(@RequestParam("courseId") int courseId) {
        Result<List<String>> result = courseService.getStudentByCourseId(courseId);
        return JSON.toJSONString(result);
    }


    @RequestMapping(value = "course/deletecoursestudent.html", method = RequestMethod.POST)
    @ResponseBody
    public String deleteCourseStudent(@RequestParam("stuId") String stuId, @RequestParam("courseId") int courseId) {
        Result<Void> result = courseService.deleteCourseStudent(stuId, courseId);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "course/addstudenttocourse.html", method = RequestMethod.POST)
    @ResponseBody
    public String addStudentToCourse(@RequestParam("file") MultipartFile file,
                                     @RequestParam("courseId") int courseId) {
        Result<Void> result = new Result<Void>();
        List<String> stuIdList = null;
        try {
             /* 从文件中读取学号 */
            if (file != null && file.getOriginalFilename().endsWith(".csv")) {
                stuIdList = readFromCSV(file);
            } else if (file != null && file.getOriginalFilename().endsWith(".xlsx")) {
                stuIdList = readFromExcel(file);
            } else {
                return JSON.toJSONString(result.status(Result.Status.ERROR));
            }
        }
        catch (Exception e) {
            logger.error("read stuId from file error!", e);
        }

        result = courseService.addStudentToCourse(stuIdList, courseId);
        return JSON.toJSONString(result);
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
