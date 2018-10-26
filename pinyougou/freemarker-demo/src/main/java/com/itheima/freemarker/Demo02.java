package com.itheima.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Demo02 {

    public static void main(String[] args) throws Exception {
       //准备数据模型
        Map<String,Object> dataMap=new HashMap<String,Object>();

        List goodsList=new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        dataMap.put("goodsList", goodsList);

        dataMap.put("username","小红");
        dataMap.put("now","2018年9月18");
        dataMap.put("success",true);


        dataMap.put("today",new Date());

        dataMap.put("point",444138798);

        //配置Freemarker
        //创建Configuration,freemarker初始化相关配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        //设置模板路径
        configuration.setDirectoryForTemplateLoading(new File("E:/IdeaWorkSpace/pinyougou/freemarker-demo/src/main/resources"));
        //设置模板内容编码
        configuration.setDefaultEncoding("utf-8");
        //创建模板对象
        Template template =configuration.getTemplate("test.ftl");


        //指定输出文件
        Writer writer = new FileWriter("D:/2.html");

        //合成输出
        template.process(dataMap,writer);

        //关闭资源
        writer.flush();
        writer.close();

    }
}
