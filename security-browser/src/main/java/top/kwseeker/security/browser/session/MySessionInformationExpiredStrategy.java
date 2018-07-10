package top.kwseeker.security.browser.session;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

public class MySessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent expiredEvent)     //session超时事件
            throws IOException {
        expiredEvent.getResponse().setContentType("application/json;charset=UTF-8");
        expiredEvent.getResponse().getWriter().write("出现并发登陆");
    }
}
