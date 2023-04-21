package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController<czq> {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    //新增
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        String key = "dish" + "_" +dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    //分页展示
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<DishDto> dishDtopage = new Page<>();

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //以菜名进行查找   过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //查询分类查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtopage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = new ArrayList<>();

        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryname = category.getName();
            dishDto.setCategoryName(categoryname);
            list.add(dishDto);
        }

        dishDtopage.setRecords(list);

        return R.success(dishDtopage);
    }

    //修改页回显显示
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        //调用dishService的方法
        DishDto byIdwithFlavor = dishService.getByIdwithFlavor(id);
        return R.success(byIdwithFlavor);
    }

    @PutMapping
    public R<String> updata(@RequestBody DishDto dishDto){

        dishService.updatewithFlavor(dishDto);
        String key = "dish" + "_" +dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    /**
     * 根据菜品分类查找菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> listR(Dish dish){
        List<DishDto> dishDtoList = new ArrayList<>();
        List<DishDto> dishDtoListredis;
        //动态设置key
        String key = "dish_" + dish.getCategoryId() +"_" +dish.getStatus();

        //先从Redis缓存中获取数据
        dishDtoListredis = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果缓存存在,直接返回,不用再查数据库
        if (dishDtoListredis != null){
            dishDtoList = dishDtoListredis;
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        queryWrapper.eq(Dish::getStatus,1);

        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(queryWrapper);

        for (Dish dish1 : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            Long id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(id != null,DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavors);


            dishDtoList.add(dishDto);
        }
        //如果缓存不存在，就查询数据库，将查询到的数据保存到redis中缓存
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }


    @DeleteMapping
    public R<String> del(Long id){
        dishService.delectWithFlavor(id);
        return R.success("删除成功");
    }
}
