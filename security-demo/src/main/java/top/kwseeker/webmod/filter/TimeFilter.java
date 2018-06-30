package top.kwseeker.webmod.filter;

import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

/**
 * 使用 Filter 实现统计从接受请求到返回结果的时间
 */

// 两种注入方式，通过 @Configuration 或者 WebMvcConfigurerAdapter FilterRegistrationBean 的 setFilter 接口
// 前者对所有请求有效
// 后者更灵活，可以指定对哪些controller有效
//@Configuration
public class TimeFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        System.out.println("TimeFilter init");

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        System.out.println("TimeFilter begin");
        long start = new Date().getTime();
        chain.doFilter(request, response);      //实际处理 request 请求过程
        System.out.println("TimeFilter 请求处理耗时" + (new Date().getTime()-start));
        System.out.println("TimeFilter end");
    }

    public void destroy() {
        System.out.println("TimeFilter destroy");
    }

}
