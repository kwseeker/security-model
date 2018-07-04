package top.kwseeker.security.core.validate.code;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import top.kwseeker.security.core.properties.SecurityProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * 将验证码验证流程以 Filter 的方式加入到 Spring Security 过滤器链里面
 */
//继承OncePerRequestFilter是为了确保每次请求只过滤一次，使用其他过滤器类在不同的web容器下过滤次数是无法确保只过滤一次的
//OncePerRequestFilter维持了是否已经或需要过滤的标志。
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private AuthenticationFailureHandler authenticationFailureHandler;
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private Set<String> urls = new HashSet<>();     //需要进行图形验证码校验的url集合
//    @Autowired
    private SecurityProperties securityProperties;  //TODO: ??? 怎么初始化的，没有注入
    private AntPathMatcher pathMatcher = new AntPathMatcher();  //可以处理正则表达式的字符串匹配工具类

    // TODO: InitializingBean 具体怎么用
    //Spring容器初始化所有属性之后执行
    //将所有需要图形验证码校验的url存入urls集合
    public void afterPropertiesSet() throws ServletException {
        logger.info("ValidateCodeFilter afterPropertiesSet() 执行...");
        super.afterPropertiesSet();
        String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                securityProperties.getValidateCode().getImage().getUrl(), ",");
        if(ArrayUtils.isNotEmpty(configUrls)){
            for(String configUrl : configUrls) {
                urls.add(configUrl);
            }
        }
        urls.add("/authentication/form");
    }

    //此方法确保单个线程的请求过滤操作只执行一次
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        boolean action = false;
        for(String url : urls) {
            if(pathMatcher.match(url, request.getRequestURI())) {
                action = true;
            }
        }

        if(action) {
//        if(StringUtils.equals("/authentication/form", request.getRequestURI())
//                && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {     //忽略大小写的比较
            try {
                validate(new ServletWebRequest(request));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    //验证用户输入的验证码与之前保存在 session 中的验证码是否相同
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {

        //获取 session 中的图片对象
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request,
                ValidateCodeController.SESSION_KEY);
        //从请求中获取用户输入的验证码参数值
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), "imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }

        if(codeInSession == null){
            throw new ValidateCodeException("验证码不存在");
        }

        if(codeInSession.isExpried()){
            sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }

        if(!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }

        sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY);
    }

    /*====================================================================================*/
    //setter and getter 方法
    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public SessionStrategy getSessionStrategy() {
        return sessionStrategy;
    }

    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
