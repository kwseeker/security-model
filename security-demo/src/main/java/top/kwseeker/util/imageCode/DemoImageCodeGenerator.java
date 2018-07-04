package top.kwseeker.util.imageCode;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import top.kwseeker.security.core.validate.code.ImageCode;
import top.kwseeker.security.core.validate.code.ValidateCodeGenerator;

//@Component("imageCodeGenerator")
public class DemoImageCodeGenerator implements ValidateCodeGenerator {

    @Override
    public ImageCode generate(ServletWebRequest request) {
        System.out.println("更高级的图形验证码生成代码");
        //TODO：更高级的图形验证码生成逻辑
        return null;
    }
}
