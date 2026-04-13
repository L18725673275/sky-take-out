package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 购物车Mapper
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据用户id、菜品id和口味查询购物车
     * @param shoppingCart
     * @return
     */
    @Select("<script>" +
            "select * from shopping_cart " +
            "where user_id = #{userId} " +
            "and dish_id = #{dishId} " +
            "<if test=\"dishFlavor != null\">" +
            "and dish_flavor = #{dishFlavor} " +
            "</if>" +
            "</script>")
    ShoppingCart selectByUserIdAndDishIdAndFlavor(ShoppingCart shoppingCart);

    /**
     * 根据用户id、套餐id查询购物车
     * @param shoppingCart
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId} and setmeal_id = #{setmealId}")
    ShoppingCart selectByUserIdAndSetmealId(ShoppingCart shoppingCart);

    /**
     * 更新购物车数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumber(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            "values (#{name}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image}, #{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id查询购物车列表
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId} order by create_time desc")
    List<ShoppingCart> selectByUserId(Long userId);

    /**
     * 根据用户id清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车数据
     * @param id
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

}