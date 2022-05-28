package com.qing.config.security;

import com.qing.config.security.filter.TokenFilter;
import com.qing.config.security.handler.*;
import com.qing.config.security.service.impl.UserDetailServiceImpl;
import com.qing.config.security.utils.MyPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

/**
 * @author qinghua
 * @date 2021/5/12 19:50
 */
@Slf4j
@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.enable}")
    private Boolean SECURITY_ENABLE;

    @Autowired
    private UserDetailServiceImpl myUserDetailsService;

    @Autowired
    private MyPasswordEncoder myPasswordEncoder;

    @Autowired
    private TokenFilter tokenFilter;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(myPasswordEncoder);
    }

    //授权
    //链式编程
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("====================security:" + SECURITY_ENABLE + "====================");
        if (SECURITY_ENABLE) {
            // 具体的权限验证配置
            this.configureDev(http);
        } else {
            // 放行所有请求
            this.configureTest(http);
        }
    }

    // 测试环境
    protected void configureTest(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;
    }

    // 开发环境
    protected void configureDev(HttpSecurity http) throws Exception {

        //解决跨域问题。cors 预检请求放行,让Spring security 放行所有preflight request（cors 预检请求）
        http.cors().and().authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        //关闭csrf防护（防止网站被攻击功能），不关闭获取不到get请求
        http.csrf().disable()
                //因为使用Token,所以把session禁用掉
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //解决iframe嵌套问题
        http.headers().frameOptions();
        http.headers().cacheControl();

        //拦截token进行权限校验,在 UsernamePasswordAuthenticationFilter 之前添加 tokenFilter
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        //首页所有人可以访问，功能页只有对应有权限的人才能访问
        //请求授权的规则
        //加了’ROLE_‘前缀需要使用hasAuthority，因为hasRole会自动加上’ROLE_‘前缀
        http.authorizeRequests()
                // 不拦截任意OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/").permitAll()
                // 暂时不对文件下载做权限要求
                .antMatchers("/download/**").permitAll()
                .anyRequest().authenticated();

        //登录认证
        http.formLogin()
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/securityUser/login").permitAll();

        // 记住我
        // http.rememberMe().rememberMeParameter("remember");

        // 注销登录
        http.logout().logoutSuccessHandler(myLogoutSuccessHandler).logoutUrl("/securityUser/logout").permitAll();

        // 处理异常情况：认证失败(未认证的用户访问自己没有权限的资源处理)和权限不足(已经认证的用户访问自己没有权限的资源处理)
        http.exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler);

    }

    @Override
    public void configure(WebSecurity web) {
        // 放行资源
        // 放行验证码（这里放行验证码还是会走token过滤器，如果携带token过期就会报错）
        web.ignoring().antMatchers("/verifyCode/**");
    }
}


