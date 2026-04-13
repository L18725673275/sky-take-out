package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import com.sky.context.BaseContext;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        
        // 转换DTO为实体
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 插入菜品信息（创建时间、更新时间、创建用户、更新用户会通过AutoFillAspect自动填充）
        dishMapper.insert(dish);
        
        // 获取插入后的菜品ID
        Long dishId = dish.getId();
        
        // 处理口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 为每个口味设置菜品ID
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 批量插入口味信息
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品：{}", dishPageQueryDTO);
        
        // 设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        
        // 执行查询
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        
        // 封装结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        
        // 检查菜品是否被套餐关联
        // 这里需要调用SetmealMapper的方法来检查，暂时跳过
        
        // 批量删除菜品的口味信息
        dishFlavorMapper.deleteByDishIds(ids);
        
        // 批量删除菜品
        dishMapper.deleteByIds(ids);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getById(Long id) {
        log.info("根据id查询菜品：{}", id);
        
        // 查询菜品信息
        Dish dish = dishMapper.selectById(id);
        
        // 查询菜品口味信息
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        
        // 转换为DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        
        // 转换DTO为实体
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        
        // 更新菜品信息
        dishMapper.update(dish);
        
        // 删除原有口味
        dishFlavorMapper.deleteByDishId(dish.getId());
        
        // 处理新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 为每个口味设置菜品ID
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            // 批量插入口味信息
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        log.info("条件查询菜品和口味：{}", dish);
        
        // 查询菜品列表
        List<Dish> dishList = dishMapper.list(dish);
        
        // 转换为DishVO列表
        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            
            // 查询菜品口味
            List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(d.getId());
            dishVO.setFlavors(flavors);
            
            dishVOList.add(dishVO);
        }
        
        return dishVOList;
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        log.info("菜品起售停售：status={}, id={}", status, id);
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

}