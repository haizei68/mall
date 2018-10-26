package com.itheima.freemarker;

import com.itheima.damain.User;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

public class Demo {

    public static void main(String[] args) throws Exception{
       //准备数据模型
        Map<String,Object> dataMap=new HashMap<String, Object>();
        dataMap.put("username","小红");

        //创建一个boolean值
        dataMap.put("success",true);
        dataMap.put("isboy",false);

        //组装List集合,然后页面输出
        List<User> users = new ArrayList<User>();

        users.add(new User(1,"小红","北京"));
        users.add(new User(2,"张三","武汉"));
        users.add(new User(3,"李四","深圳"));
        dataMap.put("users",users);

        //数字转字符
        dataMap.put("point",123456);

        //日期转换
        dataMap.put("now",new Date());

        dataMap.put("catname",null);


        //配置Freemarker
        //创建Configuration,freemarker初始化相关配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

        //设置模板路径
        configuration.setDirectoryForTemplateLoading(new File("E:/IdeaWorkSpace/pinyougou/freemarker-demo/src/main/resources"));

        //设置模板内容编码
        configuration.setDefaultEncoding("UTF-8");
        //创建模板对象
        Template template=configuration.getTemplate("test.ftl");
        //定输出文件
        Writer writer = new FileWriter("D:/1.html");
        //合成输出
        template.process(dataMap,writer);

        //关闭资源
        writer.flush();
        writer.close();
    }
}
