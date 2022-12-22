package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service//这个注解是由spirng来管理的
public class EmployeeServiceImpl extends ServiceImpl <EmployeeMapper, Employee>implements EmployeeService {//这里两个泛型一个是自己写的mapper，一个是mapper对应的实体类对象
/*
*
* 注意这里本来可以只实现EmployeeService但是一旦实现EmployeeService我们就要实现他的所有方法，
* 我们需要使用EmployeeService的方法又不重写并且又想扩展自己的方法
* 那么就可以在实现EmployeeService的同时再继承一个他的实现类也就是ServiceImpl
* */
}
