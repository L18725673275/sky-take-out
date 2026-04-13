package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺管理（用户端）
 */
@RestController
@RequestMapping("/user/shop")
@Slf4j
public class UserShopController {

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取店铺营业状态为");
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