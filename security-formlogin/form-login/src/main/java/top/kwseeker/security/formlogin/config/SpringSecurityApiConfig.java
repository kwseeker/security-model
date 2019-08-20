package top.kwseeker.security.formlogin.config;

import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring Security 支持配置多套认证规则 HttpSecurity，针对不同的路由API使用
 * 比如 SpringSecurityApiConfig 是专门给 /api/** 接口使用的
 *
 * 但是在另一个配置中添加的用户是通用的
 */
@EnableWebSecurity
@Order(90)
public class SpringSecurityApiConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")          //只用于 /api/下的接口
                .authorizeRequests()
                .anyRequest().hasRole("ADMIN")
                .and()
                .httpBasic();
    }
}
