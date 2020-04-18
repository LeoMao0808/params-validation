package person.leomao.leovalidation.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import person.leomao.leovalidation.common.model.BaseEntity;
import person.leomao.leovalidation.common.model.ResponseEntity;
import person.leomao.leovalidation.entity.UserEntity;

import javax.validation.constraints.NotBlank;

/**
 * @description: 用户控制层
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Validated //单个参数校验需要在类上面加这个注解
@RestController
@RequestMapping("/user/")
public class UserValidationController {

    /**
     * 分组校验
     * @param userEntity
     */
    @PostMapping("save")
    public ResponseEntity save(@RequestBody @Validated(BaseEntity.create.class) UserEntity userEntity){
        return ResponseEntity.ofSuccess(null);
    }

    @PostMapping("update")
    public ResponseEntity update(@RequestBody @Validated(BaseEntity.update.class) UserEntity userEntity){
        return ResponseEntity.ofSuccess(null);
    }

    /**
     * 单个参数校验
     * @param id
     */
    @GetMapping("delete")
    public ResponseEntity delete(@RequestParam @NotBlank(message = "id不能为空!") String id){
        if (StringUtils.isNotBlank(id)){
            throw new RuntimeException("test");
        }
       return ResponseEntity.ofSuccess(null);
    }
}
