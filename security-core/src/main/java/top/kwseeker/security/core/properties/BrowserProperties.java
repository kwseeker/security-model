package top.kwseeker.security.core.properties;

public class BrowserProperties {

    private String loginPage = "/login-std.html";
    private LoginType loginType = LoginType.JSON;
    private int rememberMeSeconds = 3600*24*10;   //一般都是10天左右

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public int getRememberMeSeconds() {
        return rememberMeSeconds;
    }

    public void setRememberMeSeconds(int rememberMeSeconds) {
        this.rememberMeSeconds = rememberMeSeconds;
    }
}
