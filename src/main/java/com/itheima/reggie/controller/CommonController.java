package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     *
     *文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
//        原始文件名
        String originalFilename = file.getOriginalFilename();
//        生成文件名的后缀为后续拼接做准备
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//        使用UUID生成唯一的文件名，防止文件名称重复造成覆盖。
        String fileName = UUID.randomUUID().toString()+ suffix;
//创建一个目录对象
        File dir = new File(basePath);
//        判断当前目录是否存在
if (!dir.exists()){
    //目录不存在，需要创建
    dir.mkdirs();
}

        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }
   @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");//响应一下文件类型
            int len=0;
            byte[] bytes = new byte[1024];
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);//从第一个写，写len这么长
                outputStream.flush();
            }
            outputStream.close();;
            fileInputStream.close();
        } catch (Exception e) {
           e.printStackTrace();
        }


    }
}
