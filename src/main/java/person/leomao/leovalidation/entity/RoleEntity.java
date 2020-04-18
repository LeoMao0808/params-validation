package person.leomao.leovalidation.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import person.leomao.leovalidation.common.model.BaseEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * @description: 角色Entity
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Getter
@Setter
public class RoleEntity extends BaseEntity {

    @NotBlank(message = "角色名不能为空",groups = {create.class,update.class})
    private String roleName;

    @NotNull(message = "等级不能为空！",groups = {create.class,update.class})
    @Range(min = 0,max = 3,message = "等级必须在0~3范围内",groups = {create.class,update.class})
    private Integer  level;

}
