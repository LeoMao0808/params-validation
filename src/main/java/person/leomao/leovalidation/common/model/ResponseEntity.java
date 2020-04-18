package person.leomao.leovalidation.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import person.leomao.leovalidation.common.model.enums.ResultCode;

import java.io.Serializable;

/**
 * wrapper for response entity
 *
 * @author <a href="yogurt_lei@foxmail.com">Yogurt_lei</a>
 * @version v1.0 , 2019-03-02 12:10
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEntity<T> implements Serializable {

    /**
     * response result code, not http response code
     */
    private String code = "";

    /**
     * response result code meaning
     */
    private String meaning = "";

    /**
     * response result explanation, usually used to describe exception detail
     */
    private String explanation = "";

    /**
     * response result data
     */
    private T data;

    /**
     * common success response entity
     */
    public static <T> ResponseEntity<T> ofSuccess(T data) {
        return new ResponseEntity<T>().setCode(ResultCode.OK.code).setMeaning(ResultCode.OK.meaning).setData(data);
    }

    /**
     * common failure response entity with detail failure message
     */
    public static ResponseEntity ofFailure(String explanation) {
        return new ResponseEntity().setCode(ResultCode.FAILURE.code).setMeaning(ResultCode.FAILURE.code).setExplanation(explanation);
    }

    /**
     * common failure response entity with ResultCode and detail explanation
     */
    public static ResponseEntity ofFailure(ResultCode resultCode) {
        return new ResponseEntity().setCode(resultCode.code).setMeaning(resultCode.meaning);
    }

    /**
     * common failure response entity with ResultCode and detail explanation
     */
    public static ResponseEntity ofFailure(ResultCode resultCode, String explanation) {
        return new ResponseEntity().setCode(resultCode.code).setMeaning(resultCode.meaning).setExplanation(explanation);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}