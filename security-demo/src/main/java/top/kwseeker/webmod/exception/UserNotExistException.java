package top.kwseeker.webmod.exception;

/**
 * Java库中的异常类型，通常只包含一个String类型的异常信息，也可能还包含一个Throwable cause表示导致这个异常的异常
 *
 * 实际应用中，这个异常信息可能不够用；我们就在里面添加一些额外的属性用于提供额外的信息
 * 比如用户查询时，出现异常说用户不存在，但是出现这种异常往往需要知道那个用户不存在，下面示例就是添加了用户ID的异常，
 * 抛出用户不存在的异常后，可以快速查到是哪个用户ID不存在。
 */
public class UserNotExistException extends RuntimeException {

    private static final long serialVersionUID = -6112780192479692859L;
    private String id;

    public UserNotExistException(String id) {
        super("user not exist");
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
