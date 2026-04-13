package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺管理（管理端）
 */
@RestController
@RequestMapping("/admin/shop")
@Slf4j
public class AdminShopController {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 设置店铺状态（兼容前端请求格式）
     * @param status 状态值，1表示营业，0表示打烊
     * @return
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status==1?"营业":"打烊");
        redisTemplate.opsForValue().set("shop_status", status);
        return Result.success();
    }


    /**
     * 设置店铺状态
     * @param status 状态值，1表示营业，0表示打烊
     * @return
     */
    @PutMapping("/status/{status}")
    public Result setStatusWithPath(@PathVariable Integer status) {
        log.info("设置店铺状态：{}", status==1?"营业":"打烊");
        redisTemplate.opsForValue().set("shop_status", status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Object status = redisTemplate.opsForValue().get("shop_status");
        if (status == null) {
            // 默认状态为营业
            log.info("Redis中未找到店铺状态，使用默认值：1（营业）");
            return Result.success(1);
        }
        log.info("获取店铺状态：{}", status);
        return Result.success((Integer) status);
    }

}