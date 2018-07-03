package top.kwseeker.security.browser.vo;

/**
 * 实际项目返回值可能需要各种数据以及状态，这只是最简单的原型。
 */
public class SimpleResponse {

    private Object content;

    public SimpleResponse(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
