package top.kwseeker.security.core.validate.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 图片验证码校验功能开发流程:
 * 0) 已经重新指定登录界面html文件
 * 1) 前端登录页面添加 <input><img> 元素，图形验证码生成请求 "/code/image", 用户输入验证码参数"imageCode"
 *      <input type="text" name="imageCode">
 *      <img src="/code/image">
 * 2) Controller开发
 *      针对/code/image请求的处理方法 createCode() 生成图形验证码（图片，验证码随机数，超时时间）
 *      存储在session中
 *      将图形验证码以img格式JPEG返回给前端
 * 3) Filter 开发(用户提交登陆信息，在Filter中进行验证码校验)
 *      验证成功则继续往后执行（用户名密码校验，以及成功之后的用户信息保存等等）
 *      失败则返回异常信息给前端显示
 */
@RestController
public class ValidateCodeController {

    public static final String SESSION_KEY = "SESSION_IMAGE_CODE_KEY";  //session中的属性都是键值对方式存储的

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy(); // 设置获取session属性的session工具类

    @Autowired
    private ValidateCodeGenerator imageCodeGenerator;

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = imageCodeGenerator.generate(new ServletWebRequest(request));                             //创建验证码图片
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY, imageCode);       //将图片对象存入 session 以便后面校验使用
        //TODO: request和session的关系,还没好好研究过他俩的实现与工作原理
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());        //流的方式返回图片数据给前端，前端 <img src=""/>接收
    }
}
