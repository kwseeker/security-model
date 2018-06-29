package top.kwseeker.security.browser.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()    //支持很多种认证方式，如：formLogin/csrf/rememberMe/anonymous/cors/httpBasic/openidLogin
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
    }

}
