package top.kwseeker.security.core.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 属性配置核心注册类
 * 将在application.yml中的自定义配置全部添加到这里
 */
@ConfigurationProperties(prefix = "kwseeker.security")
public class SecurityProperties {

    //Browser loginPage 和 loginType 配置项
    private BrowserProperties browser = new BrowserProperties();
    //图形验证码 width height length expireIn 配置项
    private ValidateCodeProperties validateCode = new ValidateCodeProperties();

    public BrowserProperties getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserProperties browserProperties) {
        this.browser = browserProperties;
    }

    public ValidateCodeProperties getValidateCode() {
        return validateCode;
    }

    public void setValidateCode(ValidateCodeProperties validateCode) {
        this.validateCode = validateCode;
    }
}
