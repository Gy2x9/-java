package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.web.servlet.mvc.Controller;

/**
 * 全局异常处理
 */
@RestControllerAdvice//拦截哪些注解类型的Controller
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)//如果捕捉到这个异常底下该如何处理（插入重复的用户名的时候会捕捉到这个异常）
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {//Duplicate entry 'gyq' for key 'idx_username' 对这个语句进行处理
            String[] s = ex.getMessage().split(" ");
            String s1 = s[2]+"已存在";//也就是选择gyq

            return R.error(s1);
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)//如果捕捉到这个异常底下该如何处理（插入重复的用户名的时候会捕捉到这个异常）
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
            return R.error(ex.getMessage());
        }

    }


