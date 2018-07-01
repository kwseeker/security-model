package top.kwseeker.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;
import top.kwseeker.webmod.validator.MyConstraint;

import javax.validation.constraints.Past;
import java.util.Date;

public class User {

    public interface UserSimpleView {}
    public interface UserDetailView extends UserSimpleView {}   //展示UserDetailView会自动展示UserSimpleView

    private String id;

    @MyConstraint(message = "用户名不能超过16bytes")
    @ApiModelProperty(value = "用户名")
    private String username;

    //For more Hibernate Validator to see: https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/
    @NotBlank(message = "密码不能为空")
    private String password;

    @Past(message = "生日必须是过去的时间")
    private Date birthday;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonView(UserSimpleView.class)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonView(UserSimpleView.class)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonView(UserDetailView.class)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @JsonView(UserSimpleView.class)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
