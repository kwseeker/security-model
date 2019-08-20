package top.kwseeker.security.formlogin.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录注销处理器
 */
@Slf4j
public class MyLogoutHandler implements LogoutHandler {

    /**
     * Causes a logout to be completed. The method must complete successfully.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the current principal details
     */
    public void logout(HttpServletRequest request, HttpServletResponse response,
                Authentication authentication) {
        log.info("MyLogoutHandler#logout() called");
        //这里添加一些注销后后事的处理逻辑
    }
}
