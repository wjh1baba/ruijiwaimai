package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;

import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceimpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    public void remove(Long id){
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper();
        //根据id查询
        queryWrapper1.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(queryWrapper1);
        if (count1>0){
            throw new CustomException("当前分类下关联了菜品,不能删除");
        }


        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper();
        //根据id查询
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        if (count2>0){
            throw new CustomException("当前分类下关联了套餐,不能删除");
        }

        super.removeById(id);
    }
}
