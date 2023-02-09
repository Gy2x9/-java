package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
    log.info("套餐信息：{}",setmealDto);
    setmealService.saveWithDish(setmealDto);
    return R.success("新增套餐成功");
    }


    /**
     * 套餐信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

//        构造分页构造器
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();


//        构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

//        添加过滤条件
        queryWrapper.like(name!=null, Setmeal::getName, name);

//        添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        执行分页查询
       setmealService.page(pageInfo, queryWrapper);


//        对象拷贝 这里拷贝的是分页信息，也就是totals、page、pagesize这些数据 将pageInfo的信息拷贝到dtoPage
        BeanUtils.copyProperties(pageInfo,dtoPage,"records"); //注意这里的records，比如套餐名称、图片、套餐价、售卖状态这些

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
//            将setmeal中有的属性拷贝给dto
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
//            根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);

            }
            return setmealDto;
        }).collect(Collectors.toList());//将新建的dto对象收集成一个list集合
        dtoPage.setRecords(list);
        return R.success(dtoPage);

    }
//删除套餐
    @DeleteMapping
public R<String> delete(@RequestParam List<Long> ids){
       log.info("ids:{}",ids);
       setmealService.removeWithDish(ids);
       return R.success("套餐数据删除成功");
}
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    //注意这里传过来的不是json数据 所以不用加requestbody 这里和传参方式有关系
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

}