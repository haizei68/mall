package com.pinyougou.shop.security;

import com.alibaba.fastjson.JSON;
import com.pinyougou.http.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 释放公共资源权限
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/img/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/plugins/**");
        web.ignoring().antMatchers("/*.html");
        web.ignoring().antMatchers("/seller/add.shtml");
    }

    /**
     * 拦截配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //拦截用户权限
        http.authorizeRequests()
                .antMatchers("/**")
                .access("hasAnyRole('ADMIN')");

        //禁用CSRF策略
        http.csrf().disable();

        //错误异常处理
        http.exceptionHandling().accessDeniedPage("/error.html");
        //只允许一个人登录
        http.sessionManagement().maximumSessions(1).expiredUrl("/login.html");

        //配置登录
      /*  http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html",true);*/
        http.formLogin()
                .loginPage("/shoplogin.html")
                .loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    //登录成功处理
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        //成功登录处理方式
                        Result result = new Result(true, "/admin/index.html");
                        responseLogin(response, result);
                    }
                }).failureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                //成功登录处理方式
                Result result = new Result(false, "账号或者密码不正确!");

                //设置编码同时设置JSON数据
                responseLogin(response, result);
            }
        });


        //登出
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

        //禁用头文件加载选项,解决iframe的使用问题
        http.headers().frameOptions().disable();
    }

    /***
     * 用户登录响应
     * @param response
     * @param result
     * @throws IOException
     */
    public void responseLogin(HttpServletResponse response, Result result) throws IOException {
        //设置编码同时设置JSON数据
        response.setContentType("application/json;charset=utf-8");
        //将result转JSON
        String jsonString = JSON.toJSONString(result);

        //输出JSON字符串
        PrintWriter writer = response.getWriter();
        writer.write(jsonString);

        //关闭流
        writer.flush();
        writer.close();
    }



    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * 授权配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //用户自定义认证类
        auth.userDetailsService(userDetailsService)
        .passwordEncoder(encoder);  //指定加密对象
    }
}
