package person.leomao.leovalidation.entity;

import lombok.Getter;
import lombok.Setter;
import person.leomao.leovalidation.common.custom.IdCard;
import person.leomao.leovalidation.common.model.BaseEntity;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @description: UserEntity
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @NotBlank(message = "用户名不能为空！",groups = create.class)
    private String userName;

    @NotBlank(message = "密码不能为空！",groups = {create.class,update.class})
    private String  password;

    @Email(message = "邮箱格式不合法",groups = {create.class,update.class})
    private  String  email;

    @Pattern(regexp="^1(3|4|5|7|8)\\d{9}$",message="手机号码格式错误！",groups = {create.class,update.class})
    private String phone;

    @IdCard(groups = {create.class,update.class})
    private String IdCard;

    /**
     *
     * 嵌套属性
     */
    @Valid
    @NotEmpty(message = "角色list不能为空",groups = create.class)
    private List<RoleEntity> rolesList;

}
