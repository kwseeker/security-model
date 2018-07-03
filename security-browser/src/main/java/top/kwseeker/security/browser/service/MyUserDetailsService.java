package top.kwseeker.security.browser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 用户详细信息服务
// (这个服务是应该与数据库对接的，注册用户时，用户详情信息写入数据库，
// 认证时通过loadUserByUsername从数据库获取用户信息，然后Spring security 会拿这里面的密码与登录填写的密码比对)
@Component
public class MyUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 通过用户名查询用户详细信息
     * @param username
     * @return UserDetails 是一个接口，继承此类的实例可以通过接口方法查询用户名，密码，账号是否未过期，
     * 是否未被冻结，证书是否未过期，是否不可用（删除，数据常常是假删除）。
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("登录用户名：" + username);

        String password = passwordEncoder.encode("123456");     //实际应用中这个（加密的登录密码）加密步骤应该在controller中完成
        logger.info("数据库中Bcrypt加密后密码：" + password);

        //实际应用中，这个User对象应该是从数据库查询获得的
        return new User(username, password, true, true, true, true,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }

}
