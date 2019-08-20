package top.kwseeker.security.oauth2.authenticateserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
//AuthorizationServerConfigurerAdapter可以方便的配置 OAuth2 的授权服务器, 如果启动类添加 @EnableAuthorizationServer 注解的话，
//这个bean 可以自动加入到 Spring 上下文。
//OAuth2 提供了 /oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error 等端点（请求API）
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    //TODO
    @Autowired
    private AuthenticationManager authenticationManager;

    //TODO
    @Autowired
    UserDetailsService userDetailsService;

    //token存储方式
    // InMemoryTokenStore 将token存储到内存
    @Bean
    public TokenStore memoryTokenStore() {
        return new InMemoryTokenStore();
    }

    //配置令牌端点（Token Endpoint）的安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //配置token获取和验证时的策略
        security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    //配置客户端详情服务，客户端即此 demo 中的 thirdpart-server
    //TODO：AuthorizationServerSecurityConfigurer
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")     //TODO：标示用户ID
                .authorizedGrantTypes("authorization_code", "refresh_token")    //授权码模式且支持刷新访问token
                .scopes("test")             //TODO：授权范围
                .secret(PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456"));   //TODO：客户端安全码？
    }

    //TODO: 配置授权及令牌的访问端点和令牌服务（token service）
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager).tokenStore(memoryTokenStore()).userDetailsService(userDetailsService);
    }
}
