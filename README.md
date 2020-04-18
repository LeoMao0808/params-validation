---
title：spring boot 校验参数
date：2020.04.19

---

# spring boot 校验参数

> 前言：最近项目中在使用@Validated等一系列注解来做参数校验，用的迷迷糊糊的，抽个时间来专门整理一下这些注解以及用法。

## 目录

[TOC]



##  1.常规步骤 介绍

Spring Boot里面都有什么验证呢？Spring Boot支持JSR-303验证规范，JSR是Java Specification Requests的缩写。JSR-303是Bean Validation 1.0 (JSR 303)，说白了就是基于bean的验证，更多的解释参考JCP的官网。在默认情况下，Spring Boot会引入Hibernate Validator机制来支持JSR-303验证规范。

基于JSR-303的注解有哪些，上张图，以便日后查看。更多还需参考网址：[按住CTRL ](https://www.ibm.com/developerworks/cn/java/j-lo-jsr303/index.html)

当然这些已经集成在SpringBoot中的start-web中，无需添加其他依赖了！！！

 ### 1.1 Bean Validation 中的约束

| Constraint                  |                         详细信息                         |
| --------------------------- | :------------------------------------------------------: |
| @Null                       |                 被注释的元素必须为 null                  |
| @NotNull                    |                被注释的元素必须不为 null                 |
| @AssertTrue                 |                 被注释的元素必须为 true                  |
| @AssertFalse                |                 被注释的元素必须为 false                 |
| @Min(value)                 | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| @Max(value)                 | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| @DecimalMin(value)          | 被注释的元素必须是一个数字，其值必须大于等于指定的最小值 |
| @DecimalMax(value)          | 被注释的元素必须是一个数字，其值必须小于等于指定的最大值 |
| @Size(max, min)             |           被注释的元素的大小必须在指定的范围内           |
| @Digits (integer, fraction) |   被注释的元素必须是一个数字，其值必须在可接受的范围内   |
| @Past                       |             被注释的元素必须是一个过去的日期             |
| @Future                     |             被注释的元素必须是一个将来的日期             |
| @Pattern(value)             |           被注释的元素必须符合指定的正则表达式           |

### 1.2 Hibernate Validator 附加的 constraint

| Constraint |                详细信息                |
| ---------- | :------------------------------------: |
| @Email     |     被注释的元素必须是电子邮箱地址     |
| @Length    | 被注释的字符串的大小必须在指定的范围内 |
| @NotEmpty  |        被注释的字符串的必须非空        |
| @Range     |     被注释的元素必须在合适的范围内     |

### 1.3 注意 

+ @NotNull：适用任何类型被注解的元素必须不能为null
+ @NotEmpty：适用String、 Map或者数组不能为null且长度必须大于0
+ @NotBlank：只能用在String上面，元素不能为null，调用trim()后，长度必须大于0

## 2.实操

### 2.1 准备

***

#### 2.1.1 创建BaserEntity

```java
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
```

#### 2.1.2 创建UserEntity

```java
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

    @Pattern(regexp="^1(3|4|5|7|8)\\d{9}$",message="手机号码格式错误！"
    ,groups = {create.class,update.class})
    private String phone;

    public static void main(String[] args) {
    }

}
```

#### 2.1.3 创建Controller

```java
**
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
    public ResponseEntity save(@RequestBody 
        					@Validated(BaseEntity.create.class) UserEntity userEntity){
        return ResponseEntity.ofSuccess(null);
    }

    @PostMapping("update")
    public ResponseEntity update(@RequestBody 
        					@Validated(BaseEntity.update.class) UserEntity userEntity){
        return ResponseEntity.ofSuccess(null);
    }

    /**
     * 单个参数校验
     * @param id
     */
    @GetMapping("delete")
    public ResponseEntity delete(@RequestParam 
        						 @NotBlank(message = "id不能为空!") String id){
        if (StringUtils.isNotBlank(id)){
            throw new RuntimeException("test");
        }
       return ResponseEntity.ofSuccess(null);
    }
}
```

#### 2.1.4 这里建议创建一个全局的异常捕获

```java
/**
 * @description: 全局捕获异常
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Slf4j
@ResponseStatus(HttpStatus.OK)
@RestControllerAdvice
public class GlobalExceptionHandler {
     /**
     * 参数校验异常
     */
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity validateExceptionHandler(Exception ex) {
        log.error("参数校验异常",ex);
        String msg = null;
        if (ex instanceof ValidationException){
            msg = ex.getMessage();
        }else if (ex instanceof MethodArgumentNotValidException){
            //拿到默认第一个的错误信息
            msg = ((MethodArgumentNotValidException) ex)
                    .getBindingResult()
                    .getAllErrors()
                    .get(0)
                    .getDefaultMessage();
        }
        return ResponseEntity
            .ofFailure(ResultCode.PARAMETER_VALIDATE_FAILED, msg);
    }

    /**
     * 其他未定义异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity undefinedExceptionHandler(RuntimeException ex, 						                                                    HttpServletRequest request) {
        ex.printStackTrace();
        return ResponseEntity
            .ofFailure(ResultCode.UNDEFINED_SERVER_EXCEPTION, ex.getMessage());
    }

}
```

#### 2.1.5 注意

上面代码实现了分组校验，和单个参数的校验，当然这里还创建了**ResultCode**枚举和统一返回的类**ResponseEntity**，就不在一一贴代码了！！



### 2.2单参数校验

```java
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
```

单个参数的校验的@Validated注解需要加在类上而不是参数前面！！！

### 2.3 分组校验

#### 2.3.1 BaserEntity建两个接口,并在属性约束注解上加上groups属性并指定分组

```java
@Null(message = "id需要为空！",groups = create.class)
@NotBlank(message = "id不能为空！",groups = update.class)
private String id;
```

```java
/**
 * 分组
 */
public  interface  create{}
public  interface  update{}
```



#### 2.3.2 @Validated注解指定分组

```java
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
```

#### 2.3.3 注意

测试得知在@Validated指定了那个分组，entity中没有指定groups属性的不会去校验，本来我以为不置顶就代表都会校验，但是好像不行。

### 2.4 自定义约束注解

#### 2.4.1.创建注解

```java
/**
 * @description: 校验身份证号注解
 * @author: leo.mao
 * @createDate: 2020/4/18
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdCardValidator.class)
public @interface IdCard {

    String message() default "身份证号码不合法";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

#### 2.4.2 创建自定义校验方法

```java
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
```

#### 2.4.3 测试结果示例

```json
{
"code": "1001",
"meaning": "PARAMETER VALIDATE FAILED",
"explanation": "身份证号码不合法",
"data": null
}
```

## 3. @Valid和@Validated

### 3.1 常规步骤 介绍

>Spring Validation验证框架对参数的验证机制提供了@Validated（Spring's JSR-303规范，是标准JSR-303的一个变种），javax提供了@Valid（标准JSR-303规范），配合BindingResult可以直接提供参数验证结果。在检验Controller的入参是否符合规范时，使用@Validated或者@Valid在基本验证功能上没有太多区别。但是在分组、注解地方、嵌套验证等功能上两个有所不同.

### 3.2 分组

***1. @Validated：提供了一个分组功能，可以在入参验证时，根据不同的分组采用不同的验证机制，这个网上也有资料，不详述。***

***2.@Valid：作为标准JSR-303规范，还没有吸收分组的功能。***

### 3.3 注解作用域

***1.@Validated：可以用在类型、方法和方法参数上，但是不能用在成员属性（字段）上。***

`@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})`

***2.@Valid：可以用在方法、构造函数、方法参数和成员属性（字段）上。***

`@Target({ METHOD, FIELD, CONSTRUCTOR, PARAMETER, TYPE_USE })`

***3.两者是否能用于成员属性（字段）上直接影响能否提供嵌套验证的功能。***

### 3.4 嵌套验证

#### 3.4.1 嵌套属性示例

***1.新增RoleEntity***

```java
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
    @Range(min=0,max=3,message = "等级必须在0~3范围内",groups= {create.class,update.class})
    private Integer  level;

}
```

***2. UserEntity.java中增加***

```java
/**
 * 嵌套属性
 */
@NotEmpty(message = "角色list不能为空",groups = create.class)
private List<RoleEntity> rolesList;
```

***3.验证RoleEntity中的约束注解还有用？***

1.请求参数（请注意自己json格式，因为是嵌套）

```json
{
  "userName": "demoData",
  "password": "demoData",
  "email": "1425153717@qq.com",
  "phone": "17516218809",
   "rolesList": [{
    "roleName": "",
    "level": 1
  }],
  "idCard":"410725199506167215123"
}
```

2.返回结果json

```json
{
"code": "200",
"meaning": "OK",
"explanation": "",
"data": null
}
```

***返回结果正确，没有校验到嵌套属性！！！！！！***

***4.修改UserEntity在嵌套属性上面加上@Valid注解***

```java
/**
 *
 * 嵌套属性
 */
@Valid
@NotEmpty(message = "角色list不能为空",groups = create.class)
private List<RoleEntity> rolesList;
```

请求结果：

```json
{
"code": "1001",
"meaning": "PARAMETER VALIDATE FAILED",
"explanation": "角色名不能为空",
"data": null
}
```

#### 3.4.2 总结

@Validated：用在方法入参上***无法单独提供嵌套验证功能***。不能用在成员属性（字段）上，也无法提示框架进行嵌套验证。***能配合嵌套验证注解@Valid进行嵌套验证***。

@Valid：用在方法入参上无法单独提供嵌套验证功能。能够用在成员属性（字段）上，提示验证框架进行嵌套验证。***能配合嵌套验证注解@Validated进行嵌套验证。***


## 4. 结尾（完整代码地址）

***以下地址请按住Ctrl打开！！！！***

完整代码git地址：[params-validation](https://github.com/LeoMao0808/params-validation.git)

参考资料：[SpringBoot如何优雅的校验参数](https://zhuanlan.zhihu.com/p/97555913)

​				   [Spring boot JSR-303验证实战，简单又全面](https://www.jianshu.com/p/5a99cacb3b63)

​				   [@Validated和@Valid区别](https://blog.csdn.net/qq_27680317/article/details/79970590)

嵌套报错参考： [Can not deserialize instance of out of START_ARRAY token](https://blog.csdn.net/qq_32447321/article/details/80003635)

