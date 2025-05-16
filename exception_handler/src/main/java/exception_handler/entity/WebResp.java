package exception_handler.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class WebResp<E> implements Serializable {

    private int code; // 错误码

    private int status; // HTTP状态码

    private Instant timestamp; // 时间戳

    private String message; // 错误信息

    private Map<String, Object> detail = new HashMap<>(); // 异常详情

    private E data; // 返回数据

    /**
     * HTTP请求成功
     *
     * @param message 成功信息
     * @param data 返回数据
     * @return 响应结果
     */
    public static <E> WebResp<E> success(String message, E data) {
        ZonedDateTime nowInZone = Instant.now().atZone(ZoneId.of("Asia/Shanghai"));
        return new WebResp<>(
                0, HttpStatus.OK.value(),
                nowInZone.toInstant(),
                message,
                null,
                data);
    }

    /**
     * HTTP请求失败
     *
     * @param errorCode 异常码
     *
     * @param detail 异常详情
     * @return 响应结果
     */
    public static <E> WebResp<E> failure(ErrorCode errorCode, Map<String, Object> detail) {
        ZonedDateTime nowInZone = Instant.now().atZone(ZoneId.of("Asia/Shanghai"));
        return new WebResp<>(errorCode.getCode(), errorCode.getHttpStatus().value(),
                nowInZone.toInstant(),
                errorCode.getMessage(),
                detail,
                null);
    }

}
