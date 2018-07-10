package top.kwseeker.security.browser.session;

import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyInvalidSessionStrategy extends AbstractSessionStrategy implements InvalidSessionStrategy {

    public MyInvalidSessionStrategy(String invalidSessionUrl) {
        super(invalidSessionUrl);
    }

    /**
     * session 一旦过期要做的事
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        //常见的超时处理就是跳转到登陆页面
        onSessionInvalid(request, response);
    }
}
