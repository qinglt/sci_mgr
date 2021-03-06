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

    //??????
    //????????????
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("====================security:" + SECURITY_ENABLE + "====================");
        if (SECURITY_ENABLE) {
            // ???????????????????????????
            this.configureDev(http);
        } else {
            // ??????????????????
            this.configureTest(http);
        }
    }

    // ????????????
    protected void configureTest(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll()
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);;
    }

    // ????????????
    protected void configureDev(HttpSecurity http) throws Exception {

        //?????????????????????cors ??????????????????,???Spring security ????????????preflight request???cors ???????????????
        http.cors().and().authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

        //??????csrf???????????????????????????????????????????????????????????????get??????
        http.csrf().disable()
                //????????????Token,?????????session?????????
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //??????iframe????????????
        http.headers().frameOptions();
        http.headers().cacheControl();

        //??????token??????????????????,??? UsernamePasswordAuthenticationFilter ???????????? tokenFilter
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

        //??????????????????????????????????????????????????????????????????????????????
        //?????????????????????
        //?????????ROLE_?????????????????????hasAuthority?????????hasRole??????????????????ROLE_?????????
        http.authorizeRequests()
                // ???????????????OPTIONS??????
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/").permitAll()
                // ???????????????????????????????????????
                .antMatchers("/download/**").permitAll()
                .anyRequest().authenticated();

        //????????????
        http.formLogin()
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailureHandler)
                .usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/securityUser/login").permitAll();

        // ?????????
        // http.rememberMe().rememberMeParameter("remember");

        // ????????????
        http.logout().logoutSuccessHandler(myLogoutSuccessHandler).logoutUrl("/securityUser/logout").permitAll();

        // ?????????????????????????????????(?????????????????????????????????????????????????????????)???????????????(????????????????????????????????????????????????????????????)
        http.exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler);

    }

    @Override
    public void configure(WebSecurity web) {
        // ????????????
        // ???????????????????????????????????????????????????token????????????????????????token?????????????????????
        web.ignoring().antMatchers("/verifyCode/**");
    }
}


