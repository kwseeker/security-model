package top.kwseeker.security.core.validate.code;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class ImageCode {

    private BufferedImage image;    //图片
    private String code;            //随机数
    private LocalDateTime expireTime;   //超时时间（秒）

    public ImageCode(BufferedImage image, String code, int expireIn){
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public ImageCode(BufferedImage image, String code, LocalDateTime expireTime){
        this.image = image;
        this.code = code;
        this.expireTime = expireTime;
    }

    public boolean isExpried() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

}
