package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();

        // 构建购物车对象
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setDishId(shoppingCartDTO.getDishId());
        shoppingCart.setSetmealId(shoppingCartDTO.getSetmealId());
        shoppingCart.setDishFlavor(shoppingCartDTO.getDishFlavor());

        // 查询购物车中是否已存在该商品
        ShoppingCart existingCart = null;
        if (shoppingCartDTO.getDishId() != null) {
            // 菜品
            existingCart = shoppingCartMapper.selectByUserIdAndDishIdAndFlavor(shoppingCart);
        } else if (shoppingCartDTO.getSetmealId() != null) {
                // 套餐
                existingCart = shoppingCartMapper.selectByUserIdAndSetmealId(shoppingCart);
            }

        if (existingCart != null) {
            // 已存在，数量加1
            existingCart.setNumber(existingCart.getNumber() + 1);
            shoppingCartMapper.updateNumber(existingCart);
        } else {
            // 不存在，新增购物车记录
            if (shoppingCartDTO.getDishId() != null) {
                // 菜品
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else if (shoppingCartDTO.getSetmealId() != null) {
                // 套餐
                Setmeal setmeal = setmealMapper.getSetmealById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查询购物车列表
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.selectByUserId(userId);
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }

    /**
     * 删除购物车商品
     * @param id
     */
    @Override
    public void delete(Long id) {
        shoppingCartMapper.deleteById(id);
    }

}