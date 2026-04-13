package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);

        // 转换DTO为实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 插入套餐信息
        setmealMapper.insert(setmeal);

        // 获取插入后的套餐ID
        Long setmealId = setmeal.getId();

        // 处理套餐菜品关联
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            // 为每个菜品关联设置套餐ID
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            // 批量插入套餐菜品关联
            setmealMapper.insertSetmealDish(setmealDishes);
        }
    }

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐：{}", setmealPageQueryDTO);

        // 设置分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        // 执行查询
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        // 封装结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        log.info("批量删除套餐：{}", ids);

        // 检查套餐是否被订单关联
        // 这里需要调用OrderMapper的方法来检查，暂时跳过

        // 批量删除套餐
        for (Long id : ids) {
            // 删除套餐关联的菜品
            setmealMapper.deleteSetmealDishBySetmealId(id);
            // 删除套餐
            setmealMapper.deleteById(id);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        log.info("根据id查询套餐：{}", id);

        // 查询套餐信息
        SetmealVO setmealVO = setmealMapper.getById(id);

        // 查询套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishesBySetmealId(id);

        // 设置菜品信息
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        log.info("修改套餐：{}", setmealDTO);

        // 转换DTO为实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 更新套餐信息
        setmealMapper.update(setmeal);

        // 删除原有菜品关联
        setmealMapper.deleteSetmealDishBySetmealId(setmeal.getId());

        // 处理新菜品关联
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            // 为每个菜品关联设置套餐ID
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
            // 批量插入套餐菜品关联
            setmealMapper.insertSetmealDish(setmealDishes);
        }
    }

    /**
     * 启用/禁用套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        log.info("启用/禁用套餐：status={}, id={}", status, id);

        // 检查套餐关联的菜品是否都已启用
        if (status == StatusConstant.ENABLE) {
            Setmeal setmeal = setmealMapper.getSetmealById(id);
            List<SetmealDish> setmealDishes = setmealMapper.getSetmealDishesBySetmealId(id);
            for (SetmealDish setmealDish : setmealDishes) {
                Dish dish = dishMapper.selectById(setmealDish.getDishId());
                if (dish.getStatus() == StatusConstant.DISABLE) {
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        // 更新套餐状态
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}