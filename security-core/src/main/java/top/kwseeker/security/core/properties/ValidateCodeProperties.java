package top.kwseeker.security.core.properties;

/**
 * 验证码属性类
 *
 * 这里将ImageCodeProperties又封装了一次，其实是为了以防后面有新的属性（现在是宽、高、长度、超时时间、需要使用图形验证码验证的URL）加进来
 * 到时只需要拓展一个新类，然后改一下 private ImageCodeProperties image = new ImageCodeProperties();而不需要去改
 * 代码会很长的SecurityProperties类。
 * TODO: 这里应该可以使用自定义注解让代码运行时自动判断需要使用哪个图形验证码配置实现类的
 */
public class ValidateCodeProperties {

    private ImageCodeProperties image = new ImageCodeProperties();

    private SmsCodeProperties sms = new SmsCodeProperties();

    public ImageCodeProperties getImage() {
        return image;
    }

    public void setImage(ImageCodeProperties image) {
        this.image = image;
    }

    public SmsCodeProperties getSms() {
        return sms;
    }

    public void setSms(SmsCodeProperties sms) {
        this.sms = sms;
    }
}
