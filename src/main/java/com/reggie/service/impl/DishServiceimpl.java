package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class DishServiceimpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long id = dishDto.getId();

        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavorList);
    }

    public DishDto getByIdwithFlavor(Long id){
        //查询基本信息
        Dish dish = this.getById(id);
        //条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //根据菜品id进行查询
        queryWrapper.eq(DishFlavor::getDishId,id);
        //查询口味表信息
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //创建dishDto
        DishDto dishDto = new DishDto();
        //把dish的基本信息拷贝到dishDto
        BeanUtils.copyProperties(dish,dishDto);
        //把口味信息用set的方式传到dishDto
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional  //保证数据的一致性
    public void updatewithFlavor(DishDto dishDto) {
        //更新dish表基础信息
        this.updateById(dishDto);
        //清除当前菜品表的口味信息
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(flavorLambdaQueryWrapper);
        //新增当前菜品表的口味信息
        List<DishFlavor> flavorList = dishDto.getFlavors();
        for (DishFlavor flavor : flavorList) {
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavorList);
    }

}
