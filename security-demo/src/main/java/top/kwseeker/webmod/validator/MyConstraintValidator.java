package top.kwseeker.webmod.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//自定义校验器
public class MyConstraintValidator implements ConstraintValidator<MyConstraint, Object> {
    //校验器初始化操作
    @Override
    public void initialize(MyConstraint constraint) {
        System.out.println("这里执行校验器初始化操作");
    }

    /**
     * 实现校验逻辑，比如要对用户名规范进行限制，不超过多长，不能带特殊符号等等
     * @param value 要校验的值
     * @param context 约束校验上下文
     * @return 校验符合要求返回 ture, 否则返回false
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        System.out.println("这里执行校验逻辑，成功返回true,失败返回false");
        return String.valueOf(value).length() < 16;     //假设要求value字符串长度不能超过16
    }

}