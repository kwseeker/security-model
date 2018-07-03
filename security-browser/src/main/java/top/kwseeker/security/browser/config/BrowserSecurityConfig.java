package top.kwseeker.security.browser.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.kwseeker.security.browser.authentication.MyAuthenticationFailureHandler;
import top.kwseeker.security.browser.authentication.MyAuthenticationSuccessHandler;

@EnableWebSecurity
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()    //支持很多种认证方式，如：formLogin/csrf/rememberMe/anonymous/cors/httpBasic/openidLogin
                .loginPage("/authentication/login")          //重新指定登录页面，从而取代Spring Security默认的那个简陋的页面
                .loginProcessingUrl("/authentication/form") //指定 UsernamePasswordAuthenticationFilter 认证的请求
                .successHandler(myAuthenticationSuccessHandler) //默认的处理器SimpleUrlAuthenticationFailureHandler是重定向跳转，而更常用的应该是返回JSON字段
                .failureHandler(myAuthenticationFailureHandler)
                .and()
            .authorizeRequests()
                .antMatchers("/authentication/login").permitAll()
                .antMatchers("/login-*.html").permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .csrf().disable();
    }

    // 使用 BCrypt 加密算法
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
