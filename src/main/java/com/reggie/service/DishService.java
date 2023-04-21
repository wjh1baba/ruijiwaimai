package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增
    public void saveWithFlavor(DishDto dishDto);
    //修改页页面回显
    public DishDto getByIdwithFlavor(Long id);
    //修改
    public void updatewithFlavor(DishDto dishDto);
    //删除
    public void delectWithFlavor(Long id);
}
