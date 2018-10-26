package com.itheima.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class SecurityHandler extends WebSecurityConfigurerAdapter {

    /**
     * 确定放行的连接
     * 配置放行连接
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //指定对应连接,不采取安全策略
        web.ignoring().antMatchers("/images/**");
        web.ignoring().antMatchers("/js/**");
    }

    /**
     * 核心连接配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/jsp/**").access("hasAnyRole('ADMIN')")//其他所有连接,都需要ADmin角色
                .antMatchers("/pages/**").access("hasAnyRole('USER')");//其他所有连接,都需要USER角色

        //开启登录配置
        http.formLogin()
                .loginPage("/login.html")  //指定默认登录页面
                .loginProcessingUrl("/login")   //登录处理
                .defaultSuccessUrl("/images/1.jpg",true); //登录成功后总是跳转到这个地址

        //退出session无效
        http.logout()
                .logoutUrl("/logout")   //配置登出的地址
                .invalidateHttpSession(true);   //配置登出后让session销毁

        //一个账号只能一个地方登录,其他地方登录则将该账号挤掉,被挤掉的账号需要重新登录(跳转到/login.html)
        http.sessionManagement().maximumSessions(1).expiredUrl("/login.html");

        //禁用CSRF
        http.csrf().disable();

        //错误页面
        http.exceptionHandling().accessDeniedPage("/error.html");

    }

    /**
     * 注入UserDetailsServiceImpl对象
     */
    @Autowired
    private UserDetailsService userDetailsService;


    /**
     * 构建授权信息
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("xiaohong").password("123456").roles("USER");

        //使用自定义认证类
        auth.userDetailsService(userDetailsService);
    }
}
