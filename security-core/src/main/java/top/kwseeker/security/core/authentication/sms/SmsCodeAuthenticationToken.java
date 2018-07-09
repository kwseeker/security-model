package top.kwseeker.security.core.authentication.sms;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 这个其实是一个容器类，是一种存储认证成功之后详细认证信息的数据结构（TODO: Debug抓个实例看看）
 * 认证成功之后这里面就会被进来一堆认证数据。
 */
public class SmsCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 1L;

    public SmsCodeAuthenticationToken(Object principal) {   //principal 本金（这个比喻很是恰当），返回利息 credentials
        super(principal, null);
    }
}
