package com.pinyougou.manager.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 放行公开连接
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**");
        web.ignoring().antMatchers("/img/**");
        web.ignoring().antMatchers("/js/**");
        web.ignoring().antMatchers("/plugins/**");
        web.ignoring().antMatchers("/login.html");
    }

    /**
     * 配置需要拦截的连接拦截规则
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //用户访问授权控制
        http.authorizeRequests()
                    .antMatchers("/**").access("hasAnyRole('ADMIN','USER')");
        //禁用CSRF
        http.csrf().disable();

        //发生异常
        http.exceptionHandling().accessDeniedPage("/login.html");

        //登录信息配置
        http.formLogin()
                    .loginPage("/login.html")//登录的页面
                    .loginProcessingUrl("/login")   //登录请求处理地址
                    .defaultSuccessUrl("/admin/index.html",true) //登录成功后总是跳转到该地址
                    .failureForwardUrl("/login.html");  //登录失败后跳转地址

        //登出配置
        http.logout().invalidateHttpSession(true)   //登出之后session销毁
            .logoutSuccessUrl("/login.html")        //登出后跳转的地址
            .logoutUrl("/logout");                  //处理登出的请求地址


        //用户在第一个方登录,如果该账号在另外一个地方登录
        http.sessionManagement().maximumSessions(1).expiredUrl("/login.html");

        //禁用头文件加载,解决iframe不可用的问题
        http.headers().frameOptions().disable();

    }

    /**
     * 授权配置
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //创建用户存于内存中
        auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
    }
}
