package top.kwseeker.security.formlogin.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {

    private String name;
    private String password;
    private String[] roles;
}
