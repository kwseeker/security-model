package top.kwseeker.security.core.authentication.sms;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import top.kwseeker.security.core.properties.SecurityConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 定义一个新的 Filter 用于手机短信认证码校验
 *
 * 1）继承 AbstractAuthenticationProcessingFilter, Security 所有认证过滤器均继承这个类；
 * 2）实现 attemptAuthentication() 方法，方法中实现过滤的具体逻辑（是否是Post请求、手机号码是否不为空，，
 *    最后交给 AuthenticationManager 调自定义的认证器 SmsCodeAuthenticationProvider 认证）；
 * 3）SmsCodeAuthenticationProvider 认证
 */
public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private String mobileParameter = SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE;

    public SmsCodeAuthenticationFilter() {
        super(new AntPathRequestMatcher(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE, "POST"));
        System.out.println("自定义手机验证码校验监听： " + SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if(!StringUtils.equalsIgnoreCase("post", request.getMethod())) {
            throw new AuthenticationServiceException("短信认证登录请求只支持POST方法");
        }

        String mobile = ServletRequestUtils.getStringParameter(request, "mobile");
        if(StringUtils.isBlank(mobile)) {
            throw new AuthenticationServiceException("短信认证登录请求手机号不能为空");
        }

        SmsCodeAuthenticationToken smsCodeAuthenticationToken = new SmsCodeAuthenticationToken(mobile);     //付本金
        smsCodeAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));           //从request中获取请求信息加到 token 中
        return this.getAuthenticationManager().authenticate(smsCodeAuthenticationToken);
    }

}
