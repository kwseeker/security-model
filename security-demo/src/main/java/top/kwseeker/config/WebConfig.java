package top.kwseeker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import top.kwseeker.webmod.filter.TimeFilter;
import top.kwseeker.webmod.interceptor.TimeInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private TimeInterceptor timeInterceptor;

    /**
     * 通过 FilterRegistrationBean 可以设置 Filter 对哪些URL起作用
     */
    @Bean
    public FilterRegistrationBean timeFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        TimeFilter timeFilter = new TimeFilter();
        registrationBean.setFilter(timeFilter);

        List<String> urls = new ArrayList<>();
//        urls.add("/*");                             //针对所有URL起作用
//        urls.add("/user/*");
        urls.add("/hello/*");
        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }

    /**
     * 拦截器对所有请求起作用
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeInterceptor);
    }

}
