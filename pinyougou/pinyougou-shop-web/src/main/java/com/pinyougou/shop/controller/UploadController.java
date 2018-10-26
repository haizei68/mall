package com.pinyougou.shop.controller;

import com.pinyougou.fastdfs.UploadUtil;
import com.pinyougou.http.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传
 */
@RestController
public class UploadController {

    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/upload")
    public Result upload(MultipartFile file) throws Exception{
        //获取文件的名字
        String originalFilename = file.getOriginalFilename();

        //截取方式获取后缀
        String suffix = StringUtils.getFilenameExtension(originalFilename);

        //文件的字节数组/扩展名
        byte[] bytes=file.getBytes();

        //FastDFS   String conffilename,byte[] buffer,String suffix
        String[] upload = UploadUtil.upload("classpath:config/tracker.conf", bytes, suffix);

        //拼接图片的地址
        String  url="http://192.168.211.128/"+upload[0]+"/"+upload[1];

        return new Result(true,url);
    }
}
