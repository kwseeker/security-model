package top.kwseeker.security.core.validate.code.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("smsCodeSender")
public class DefaultSmsCodeSenderImpl implements SmsCodeSender {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void send(String mobile, String code) {
        // 给号码为 mobile 的手机发送内容为 code 的短信
        logger.info("手机号码：" + mobile + " 短信验证码：" + code);
    }
}
