package top.kwseeker.security.core.validate.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import top.kwseeker.security.core.validate.code.sms.SmsCodeSender;

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
 *
 * 短信验证码校验流程：
 *      用户进入登录页面，登录页面点击发送验证码的连接，进入此controller createSmsCode()方法, 生成短信验证码并发送给指定手机号
 *      用户收到短信验证码输入提交，进入 /authentication/mobile 对应的controller 方法，比对手机号和验证码
 */
@RestController
public class ValidateCodeController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SESSION_KEY_FOR_CODE_IMAGE = "SESSION_IMAGE_CODE_KEY";  //session中的属性都是键值对方式存储的
    public static final String SESSION_KEY_FOR_CODE_SMS = "SESSION_SMS_CODE_KEY";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy(); // 设置获取session属性的session工具类

    @Autowired
    private ValidateCodeGenerator imageCodeGenerator;

    @Autowired
    private ValidateCodeGenerator smsCodeGenerator;
    @Autowired
    private SmsCodeSender smsCodeSender;

    //生成图片验证码并返回给前端
    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = (ImageCode)imageCodeGenerator.generate(new ServletWebRequest(request));                             //创建验证码图片
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_FOR_CODE_IMAGE, imageCode);       //将图片对象存入 session 以便后面校验使用
        //TODO: request和session的关系,还没好好研究过他俩的实现与工作原理
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());        //流的方式返回图片数据给前端，前端 <img src=""/>接收
    }

    //生成短信验证码
    @GetMapping("/code/sms")
    public void createSmsCode(HttpServletRequest request) throws ServletRequestBindingException {
        logger.info("生成短信验证码...");
        //生成短信验证码并存入session
        ValidateCode smsCode = smsCodeGenerator.generate(new ServletWebRequest(request));
        logger.info("生成的短信验证码： " + smsCode.getCode());
        sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_FOR_CODE_SMS, smsCode);
        //发送包含验证码的短信
        String mobile = ServletRequestUtils.getRequiredStringParameter(request,"mobile");
        smsCodeSender.send(mobile, smsCode.getCode());
    }

}
