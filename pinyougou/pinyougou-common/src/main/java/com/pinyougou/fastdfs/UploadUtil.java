package com.pinyougou.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/**
 * 实现fastDFS文件上传
 */
public class UploadUtil {
    /**
     * 远程文件上传
     * @param conffilename  tracker的服务链接信息
     * @param buffer         要上传的文件的字节数组
     * @param suffix        要上传的文件的后缀
     * @return
     * @throws Exception
     */
    public static String[] upload(String conffilename,byte[] buffer,String suffix) throws Exception {
        //解析类路径
        conffilename=getConfig(conffilename);



        //初始化
        ClientGlobal.init(conffilename);

        //创建Tracker客户端
        TrackerClient trackerClient = new TrackerClient();

        //链接Tracker获得TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过TrackerServer来创建一个Storage客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        //通过Storage客户端实现文件上传
        String[] strings = storageClient.upload_file(buffer,suffix,null);

        return strings;
    }

    /**
     * 获取类路径文件
     * @param path
     * @return
     */
    public static String getConfig(String path){
        //类路径下的文件
        if (path.contains("classpath:")){
            path = path.replace("classpath:",UploadUtil.class.getResource("/").getPath());

        }
        return path;
    }

    public static void main(String[] args) throws Exception{
        //String tracker="E:\\IdeaWorkSpace\\pinyougou\\pinyougou-common\\src\\main\\resources\\tracker.conf";
        //String filename="E:\\IdeaWorkSpace\\pinyougou\\pinyougou-common\\src\\main\\resources\\2.jpg";

       /* String tracker="classpath:tracker.conf";
        String filename="classpath:2.jpg";

        String[] upload = upload(tracker, filename);
        for (String s : upload) {
            System.out.println(s);
        }*/
    }
}
