package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计营业额
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计VO
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //存放开始到结束时间之间的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }


        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);
            //根据日期查询营业额
            Double turnover = orderMapper.sumByMap(map);
            //如果营业额为null，说明该日期没有订单，营业额为0
            if(turnover==null){
                turnover=0.0;
            }
            turnoverList.add(turnover);
        }


        //封装返回结果
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 统计用户数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计VO
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //存放开始到结束时间之间的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每日新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //存放每日累计用户总数
        List<Integer> totalUserList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //统计当日新增用户数量
            Integer newUser = userMapper.countByCreateTime(beginTime, endTime);
            if(newUser == null) {
                newUser = 0;
            }
            newUserList.add(newUser);

            //统计截止到当日的累计用户总数
            Integer totalUser = userMapper.countTotalByTime(endTime.plusNanos(1));
            if(totalUser == null) {
                totalUser = 0;
            }
            totalUserList.add(totalUser);
        }

        //封装返回结果
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 统计订单数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计VO
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //存放开始到结束时间之间的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //存放每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for(LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //统计当日订单总数
            Integer orderCount = orderMapper.countByTime(beginTime, endTime);
            if(orderCount == null) {
                orderCount = 0;
            }
            orderCountList.add(orderCount);

            //统计当日有效订单数（已完成订单）
            Integer validOrderCount = orderMapper.countValidByTime(beginTime, endTime, Orders.COMPLETED);
            if(validOrderCount == null) {
                validOrderCount = 0;
            }
            validOrderCountList.add(validOrderCount);
        }

        //统计时间段内的订单总数
        LocalDateTime totalBeginTime = LocalDateTime.of(dateList.get(0), LocalTime.MIN);
        LocalDateTime totalEndTime = LocalDateTime.of(dateList.get(dateList.size() - 1), LocalTime.MAX);
        Integer totalOrderCount = orderMapper.countTotalByTime(totalBeginTime, totalEndTime);
        if(totalOrderCount == null) {
            totalOrderCount = 0;
        }

        //统计时间段内的有效订单总数
        Integer validOrderCount = orderMapper.countValidTotalByTime(totalBeginTime, totalEndTime, Orders.COMPLETED);
        if(validOrderCount == null) {
            validOrderCount = 0;
        }

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount > 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        //封装返回结果
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计销量排名前10
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销量排名VO
     */
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询销量排名前10的商品
        List<Map<String, Object>> salesTop10List = orderMapper.getSalesTop10(beginTime, endTime, Orders.COMPLETED);

        //提取商品名称和销量
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for(Map<String, Object> map : salesTop10List) {
            String name = (String) map.get("name");
            Object numberObj = map.get("number");
            Integer number = null;
            if(numberObj != null) {
                if(numberObj instanceof BigDecimal) {
                    number = ((BigDecimal) numberObj).intValue();
                } else if(numberObj instanceof Long) {
                    number = ((Long) numberObj).intValue();
                } else if(numberObj instanceof Integer) {
                    number = (Integer) numberObj;
                } else if(numberObj instanceof Number) {
                    number = ((Number) numberObj).intValue();
                }
            }
            if(name != null && number != null) {
                nameList.add(name);
                numberList.add(number);
            }
        }

        //封装返回结果
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}