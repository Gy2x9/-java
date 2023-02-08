package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService <Setmeal>{
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public  void saveWithDish(SetmealDto setmealDto);
//删除套餐的时候删除菜品的关联数据 也是要操作两张表
    public void removeWithDish(List<Long> ids);
}

