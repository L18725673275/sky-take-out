package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计营业额
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计VO
     */
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计用户数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户统计VO
     */
    UserReportVO userStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计订单数据
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计VO
     */
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计销量排名前10
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销量排名VO
     */
    SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end);
}