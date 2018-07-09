package top.kwseeker.security.core.properties;

/**
 * 验证码属性实现类
 */
public class ImageCodeProperties extends SmsCodeProperties {

    private int width = 67;
    private int height =23;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
