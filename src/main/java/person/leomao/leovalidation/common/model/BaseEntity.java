package person.leomao.leovalidation.common.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

/**
 * @description: BaseEntity
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Getter
@Setter
public class BaseEntity {

    @Null(message = "id需要为空！",groups = create.class)
    @NotBlank(message = "id不能为空！",groups = update.class)
    private String id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer delFlag;

    /**
     * 分组
     */
    public  interface  create{}
    public  interface  update{}
}
