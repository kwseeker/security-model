package top.kwseeker.security.browser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import top.kwseeker.security.browser.authentication.MyAuthenticationFailureHandler;
import top.kwseeker.security.browser.authentication.MyAuthenticationSuccessHandler;
import top.kwseeker.security.core.authentication.sms.SmsCodeAuthenticationConfig;
import top.kwseeker.security.core.properties.SecurityProperties;
import top.kwseeker.security.core.validate.code.SmsCodeFilter;
import top.kwseeker.security.core.validate.code.ValidateCodeFilter;

import javax.sql.DataSource;

@EnableWebSecurity
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SmsCodeAuthenticationConfig smsCodeAuthenticationConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        logger.info("RememberMe Token超时时间： " + securityProperties.getBrowser().getRememberMeSeconds());

        http.apply(smsCodeAuthenticationConfig)
                .and()
            .formLogin()    //支持很多种认证方式，如：formLogin/csrf/rememberMe/anonymous/cors/httpBasic/openidLogin
                .loginPage("/authentication/login")          //重新指定登录页面，从而取代Spring Security默认的那个简陋的页面(可以是controller请求路径也可以是.html文件路径)
                .loginProcessingUrl("/authentication/form") //指定 UsernamePasswordAuthenticationFilter 认证的请求
                .successHandler(myAuthenticationSuccessHandler) //默认的处理器SimpleUrlAuthenticationFailureHandler是重定向跳转，而更常用的应该是返回JSON字段
                .failureHandler(myAuthenticationFailureHandler)
                .and()
            .rememberMe()
                .tokenRepository(persistentTokenRepository())   //TODO: 为什么这里还是需要调接口？
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())   //过期时间
                .userDetailsService(userDetailsService)     //指定用于查找UserDetails信息的UserDetailsService实例
                .and()
            .authorizeRequests()
                .antMatchers("/authentication/login",
                        securityProperties.getBrowser().getLoginPage(),
                        "/code/image",
                        "/code/sms").permitAll()
                .anyRequest()
                .authenticated()
                .and()
//            .logout()
//                .logoutUrl("/authentication/logout")
//                .logoutSuccessUrl("/logout-std.html")
//                .logoutSuccessHandler()
//                .invalidateHttpSession(true)
//                .addLogoutHandler()
//                .deleteCookies()
//                .and()
            .csrf().disable();

        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        validateCodeFilter.setSecurityProperties(securityProperties);
        validateCodeFilter.afterPropertiesSet();    //TODO: 不是自动执行的么？

        SmsCodeFilter smsCodeFilter = new SmsCodeFilter();
        smsCodeFilter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        smsCodeFilter.setSecurityProperties(securityProperties);
        smsCodeFilter.afterPropertiesSet();
    }

    // 使用 BCrypt 加密算法
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //RememberMe功能存储用户Token的Repository
    //JdbcTokenRepositoryImpl 这个类可以根据 application.yml 中配置的数据源，自动创建存储token数据的表，
    // 并提供初始化、创建、删除、更新 token 的方法
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        logger.info("persistentTokenRepository() 初始化");
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);  //建表应该放在项目部署的脚本里面
        return tokenRepository;
    }
}
