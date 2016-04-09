package com.liujan.mapper;

import com.liujan.entity.Face;
import com.liujan.entity.FaceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface FaceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int countByExample(FaceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int deleteByExample(FaceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int deleteByPrimaryKey(String photo);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int insert(Face record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int insertSelective(Face record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    List<Face> selectByExample(FaceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    Face selectByPrimaryKey(String photo);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int updateByExampleSelective(@Param("record") Face record, @Param("example") FaceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int updateByExample(@Param("record") Face record, @Param("example") FaceExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int updateByPrimaryKeySelective(Face record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table face
     *
     * @mbggenerated Sat Apr 09 13:42:28 CST 2016
     */
    int updateByPrimaryKey(Face record);
}