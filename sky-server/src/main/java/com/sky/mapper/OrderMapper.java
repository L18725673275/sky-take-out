package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    void update(Orders orders);

    List<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    /**
     * 根据动态条件统计营业额
     * @param map
     * @return 营业额
     */
    Double sumByMap(Map map);

    /**
     * 根据日期统计订单总数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_time >= #{beginTime} AND order_time < #{endTime}")
    Integer countByTime(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据日期统计有效订单数（已完成订单）
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 有效订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_time >= #{beginTime} AND order_time < #{endTime} AND status = #{status}")
    Integer countValidByTime(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 统计指定时间段内的订单总数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 订单总数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_time >= #{beginTime} AND order_time < #{endTime}")
    Integer countTotalByTime(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 统计指定时间段内的有效订单总数
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return 有效订单数
     */
    @Select("SELECT COUNT(*) FROM orders WHERE order_time >= #{beginTime} AND order_time < #{endTime} AND status = #{status}")
    Integer countValidTotalByTime(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

    /**
     * 查询销量排名前10的商品
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（已完成）
     * @return 商品销量列表，包含name和number字段
     */
    List<Map<String, Object>> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime, Integer status);

}