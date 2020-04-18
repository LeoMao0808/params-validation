package person.leomao.leovalidation.common.validator;

import cn.hutool.core.util.IdcardUtil;
import org.apache.commons.lang3.StringUtils;
import person.leomao.leovalidation.common.custom.IdCard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description: 身份证校验方法
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
public class IdCardValidator implements ConstraintValidator<IdCard,String> {

    @Override
    public void initialize(IdCard constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //参数为空不校验
        if (StringUtils.isEmpty(value)){
            return true;
        }
        return IdcardUtil.isValidCard(value);
    }
}
