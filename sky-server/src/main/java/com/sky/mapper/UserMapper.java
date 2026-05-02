package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);

    /**
     * 根据日期统计新增用户数量
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 新增用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE create_time >= #{beginTime} AND create_time < #{endTime}")
    Integer countByCreateTime(java.time.LocalDateTime beginTime, java.time.LocalDateTime endTime);

    /**
     * 统计指定日期之前的用户总数
     * @param endTime 结束时间
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM user WHERE create_time < #{endTime}")
    Integer countTotalByTime(java.time.LocalDateTime endTime);

}