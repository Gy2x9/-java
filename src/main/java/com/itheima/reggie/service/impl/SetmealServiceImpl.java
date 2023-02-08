package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */

   @Transactional//注意这里要操作两张表，所以要保证事务一致性
    public void saveWithDish(SetmealDto setmealDto) {
    //保存套餐的基本信息，操作setmeal，执行insert操作
    this.save(setmealDto);

       List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
       //保存套餐和菜品的关联信息，操作setmeal_dish执行insert操作
       setmealDishService.saveBatch(setmealDishes);
    }
//因为操作了两张表所以为了保持数据一致性要用事务注解
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//       select count(*) from setmeal where id in (1,2,3) and status=1
//查询套餐状态，确定是否可用删除 status=1是售卖中所以不能删除
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count =this.count(queryWrapper);
        if(count>0){
//            如果不能删除则抛出一个异常
            throw new CustomException("套餐正在售卖中，无法删除");
        }
//        如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);


//        delete from setmeal_dish where setmeal_id in(1,2,3)
//        注意这个方法传入进来的ids里面装的是套餐的id 不是每个套餐里面菜品的id
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
//        删除关系表中的数据---setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);


    }
}
