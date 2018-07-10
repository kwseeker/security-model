package top.kwseeker.security.browser.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import top.kwseeker.security.browser.session.MyInvalidSessionStrategy;
import top.kwseeker.security.browser.session.MySessionInformationExpiredStrategy;
import top.kwseeker.security.core.properties.SecurityProperties;

@Configuration
public class BrowserSecurityBeanConfig {
    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    @ConditionalOnMissingBean(InvalidSessionStrategy.class)
    public InvalidSessionStrategy invalidSessionStrategy(){
//        return new MyInvalidSessionStrategy(securityProperties.getBrowser().getSession().getSessionInvalidUrl());
        return new MyInvalidSessionStrategy("/session-invalid.html");
    }

    @Bean
    @ConditionalOnMissingBean(SessionInformationExpiredStrategy.class)
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy(){
//        return new MySessionInformationExpiredStrategy(securityProperties.getBrowser().getSession().getSessionInvalidUrl());
        return new MySessionInformationExpiredStrategy();
    }
}
