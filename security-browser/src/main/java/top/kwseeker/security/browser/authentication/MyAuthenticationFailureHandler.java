package top.kwseeker.security.browser.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import top.kwseeker.security.core.properties.LoginType;
import top.kwseeker.security.core.properties.SecurityProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败后处理（通过HttpServletResponse返回JSON格式的错误信息 或者 重定向到错误界面）
 * TODO: 怎么执行到这里的（系统发出了什么认证失败的信息，信息又是怎么被抓取的，最后怎么调到这个 Handler 的 onAuthenticationFailure 方法的）？
 */
@Component
public class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * AuthenticationException 继承 RuntimeException, 两个构造器方法均包含 String 类型错误信息
     */
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        logger.info("登录失败");

        if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())) { //返回JSON格式错误信息
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(exception));
        } else {                                                                    //重定向到错误界面
            super.onAuthenticationFailure(request, response, exception);            //Spring框架默认就是这么处理的
        }

    }
}
