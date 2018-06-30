package top.kwseeker.webmod.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

//自定义的 Interceptor 需要通过 WebMvcConfigurerAdapter addInterceptors() 方法注册
@Component
public class TimeInterceptor implements HandlerInterceptor {

    // For details to read source code : org.springframework.web.servlet.HandlerInterceptor

    /**
     * @param handler 是 controller 中处理请求的方法
     * @return 返回值用于决定是否执行请求的controller方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("TimeInterceptor preHandler");
        System.out.println("handler方法： " + ((HandlerMethod)handler).getBean().getClass().getName() + "."
            + ((HandlerMethod)handler).getMethod().getName());
        request.setAttribute("startTime", new Date().getTime());
        return true;    // 返回true调用handler方法，否则不调用。
    }

    /**
     * @param var4 controller 方法返回的 ModelAndView 结果
 *             内部包含一个Object类型的view，一个ModelMap类型的model，返回的HTTP status值，还有一个标记是否被clear()方法清除的cleared变量。
 *             如果方法没有返回这种类型的值，则为 null 。
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView var4) throws Exception {
        System.out.println("TimeInterceptor postHandle");
        Long start = (Long) request.getAttribute("startTime");
        System.out.println("TimeInterceptor 请求返回成功耗时：" + (new Date().getTime() - start));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception exception) throws Exception {
        System.out.println("TimeInterceptor afterCompletion");
        Long start = (Long) request.getAttribute("startTime");
        System.out.println("TimeInterceptor 请求结束耗时：" + (new Date().getTime() - start));
        System.out.println("exception is " + exception);
    }
}
