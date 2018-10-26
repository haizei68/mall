package com.pinyougou.manager.controller;

import com.pinyougou.fastdfs.UploadUtil;
import com.pinyougou.http.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/6 12:16
 *
 ****/
@RestController
public class UploadController {

    //记录tracker文件存储地址
    @Value("${TRACKER_PATH}")
    private String TRACKER_PATH;

    //记录对应域名
    @Value("${FASTDFS_DOMAIN}")
    private String FASTDFS_DOMAIN;

    /***
     * 文件上传
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload")
    public Result upload(MultipartFile file) throws Exception{
        //文件名
        String originalFilename = file.getOriginalFilename();

        //获取文件后缀
        String suffix = StringUtils.getFilenameExtension(originalFilename);

        //FastDFS
        //String[] uploads = UploadUtil.upload("classpath:config/tracker.conf", file.getBytes(), suffix);
        String[] uploads = UploadUtil.upload(TRACKER_PATH, file.getBytes(), suffix);

        //拼接一个URL地址
        //String url ="http://192.168.211.128/"+uploads[0]+"/"+uploads[1];
        String url =FASTDFS_DOMAIN+uploads[0]+"/"+uploads[1];

        //封装一个Result
        return new Result(true, url);
    }


}
