package top.kwseeker.security.formlogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.kwseeker.security.formlogin.handler.MyLogoutHandler;
import top.kwseeker.security.formlogin.handler.MyLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
//@Order(100)   //优先级默认是100，值越小优先级越高
public class SpringSecurityCommonConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义 AuthenticationManager
     *
     * Override this method to expose the {@link AuthenticationManager} from
     * {@link #configure(AuthenticationManagerBuilder)} to be exposed as a Bean. For
     * example:
     *
     * <pre>
     * &#064;Bean(name name="myAuthenticationManager")
     * &#064;Override
     * public AuthenticationManager authenticationManagerBean() throws Exception {
     *     return super.authenticationManagerBean();
     * }
     * </pre>
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();   //此处应根据业务自定义 AuthenticationManager Bean
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();  //此处应根据业务自定义 UserDetailService Bean
    }

    /**
     * Used by the default implementation of {@link #authenticationManager()} to attempt
     * to obtain an {@link AuthenticationManager}. If overridden, the
     * {@link AuthenticationManagerBuilder} should be used to specify the
     * {@link AuthenticationManager}.
     *
     * <p>
     * The {@link #authenticationManagerBean()} method can be used to expose the resulting
     * {@link AuthenticationManager} as a Bean. The {@link #userDetailsServiceBean()} can
     * be used to expose the last populated {@link UserDetailsService} that is created
     * with the {@link AuthenticationManagerBuilder} as a Bean. The
     * {@link UserDetailsService} will also automatically be populated on
     * {@link HttpSecurity#getSharedObject(Class)} for use with other
     * {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
     * </p>
     *
     * <p>
     * For example, the following configuration could be used to register in memory
     * authentication that exposes an in memory {@link UserDetailsService}:
     * </p>
     *
     * <pre>
     * &#064;Override
     * protected void configure(AuthenticationManagerBuilder auth) {
     * 	auth
     * 	// enable in memory based authentication with a user named
     * 	// &quot;user&quot; and &quot;admin&quot;
     * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
     * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
     * }
     *
     * // Expose the UserDetailsService as a Bean
     * &#064;Bean
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     *
     * </pre>
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(getPasswordEncoder())
                //假设这是个OA系统
                .withUser("Arvin").password(getPasswordEncoder().encode("1234561")).roles("ADMIN", "STAFF")    //系统管理员+员工
                //企业管理者
                .and().withUser("Bob").password(getPasswordEncoder().encode("1234562")).roles("MANAGER")    //经理
                //普通职员
                .and().withUser("Cindy").password(getPasswordEncoder().encode("1234563")).roles("STAFF");   //员工
    }

    /**
     * 自定义配置 WebSecurity: 要想自定义配置先要知道这个类都做了啥,还好注释比较详细，人家甚至在注释里面都把示例写进去了
     *
     * 1) List<RequestMatcher>
     * 2) List<SecurityBuilder<? extends SecurityFilterChain>>
     * 3) IgnoredRequestConfigurer
     * 4) FilterSecurityInterceptor
     * 5) HttpFirewall
     * 6) WebInvocationPrivilegeEvaluator
     * 7) DefaultWebSecurityExpressionHandler
     * 8) SecurityExpressionHandler<FilterInvocation>
     *
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**")
                .antMatchers("/actuator/**")
                .antMatchers("/user/login");
    }

    /**
     * HttpSecurity 是配置 http请求的安全策略的类
     *
     * Override this method to configure the {@link HttpSecurity}. Typically subclasses
     * should not invoke this method by calling super as it may override their
     * configuration. The default configuration is:
     *
     * 默认配置是对所有的请求都要认证，提供了 formLogin()表单认证 和 Basic 认证
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                    //.loginPage("")        //实际项目是应该自己指定登录页面的，而不是用默认的页面
                .and()
                .logout().permitAll(); //使能退出登录, WebSecurityConfigurerAdapter 默认是实现了的
                    //.logoutUrl("/my/logout")
                    //.logoutSuccessHandler(new MyLogoutSuccessHandler()) //默认是用的？
                    //.addLogoutHandler(new MyLogoutHandler());           //默认是用的？
                    //.deleteCookies();
    }

}
