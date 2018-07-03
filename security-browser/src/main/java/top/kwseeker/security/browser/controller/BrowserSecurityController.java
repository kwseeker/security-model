package top.kwseeker.security.browser.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import top.kwseeker.security.browser.vo.SimpleResponse;
import top.kwseeker.security.core.properties.SecurityProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求缓存：https://docs.spring.io/spring-security/site/docs/current/reference/html5/#exception-translation-filter 15.2.3. SavedRequest s and the RequestCache Interface
 * HttpSessionRequestCache 是 RequestCache 默认实现：
 * API 文档：https://docs.spring.io/spring-security/site/docs/4.2.5.RELEASE/apidocs/org/springframework/security/web/savedrequest/HttpSessionRequestCache.html
 */
@RestController
public class BrowserSecurityController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();  //DefaultRedirectStrategy sendRedirect() 包装了servlet response.sendRedirect()
                                                                                //添加了路径校验修改和判断是否需要加入SessionID的过程

    @Autowired
    private SecurityProperties securityProperties;

    @RequestMapping("/authentication/login")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public SimpleResponse loginAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            logger.info("引发跳转的URL: " + targetUrl);
            if(StringUtils.endsWithIgnoreCase(targetUrl, ".html")) {    //如果请求末尾加着.html重定向到配置的登录页面
                redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getLoginPage());
            }
        }

        return new SimpleResponse("访问的服务需要身份认证，引导用户到登录页...");
    }

}
