package com.liujan.mapper;

import com.liujan.entity.Statistic;
import com.liujan.entity.StatisticExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StatisticMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int countByExample(StatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int deleteByExample(StatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int deleteByPrimaryKey(Integer statisticId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int insert(Statistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int insertSelective(Statistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    List<Statistic> selectByExample(StatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    Statistic selectByPrimaryKey(Integer statisticId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int updateByExampleSelective(@Param("record") Statistic record, @Param("example") StatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int updateByExample(@Param("record") Statistic record, @Param("example") StatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int updateByPrimaryKeySelective(Statistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table statistic
     *
     * @mbggenerated Wed May 11 10:10:34 CST 2016
     */
    int updateByPrimaryKey(Statistic record);
}