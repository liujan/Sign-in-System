package com.liujan.entity;

import java.util.Date;

public class Statistic {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.statistic_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private Integer statisticId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.stu_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private String stuId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.course_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private Integer courseId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.sigin_time
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private Date siginTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.week
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private int week;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column statistic.confidence
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    private Double confidence;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.statistic_id
     *
     * @return the value of statistic.statistic_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public Integer getStatisticId() {
        return statisticId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.statistic_id
     *
     * @param statisticId the value for statistic.statistic_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setStatisticId(Integer statisticId) {
        this.statisticId = statisticId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.stu_id
     *
     * @return the value of statistic.stu_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public String getStuId() {
        return stuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.stu_id
     *
     * @param stuId the value for statistic.stu_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.course_id
     *
     * @return the value of statistic.course_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.course_id
     *
     * @param courseId the value for statistic.course_id
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.sigin_time
     *
     * @return the value of statistic.sigin_time
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public Date getSiginTime() {
        return siginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.sigin_time
     *
     * @param siginTime the value for statistic.sigin_time
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setSiginTime(Date siginTime) {
        this.siginTime = siginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.week
     *
     * @return the value of statistic.week
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public int getWeek() {
        return week;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.week
     *
     * @param week the value for statistic.week
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column statistic.confidence
     *
     * @return the value of statistic.confidence
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public Double getConfidence() {
        return confidence;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column statistic.confidence
     *
     * @param confidence the value for statistic.confidence
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}