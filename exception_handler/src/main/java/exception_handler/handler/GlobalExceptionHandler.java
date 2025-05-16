package exception_handler.handler;

import exception_handler.entity.ErrorCode;
import exception_handler.entity.WebResp;
import exception_handler.exception.MyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理全局异常
     *
     * @param e 异常
     * @return WebResp
     */
    @ExceptionHandler(Exception.class)
    public WebResp<?> handleException(Exception e, HttpServletRequest request) {
        log.error("\u001B[31m请求：\u001B[34m{}\u001B[31m 发生全局异常，详细信息：\u001B[0m", request.getRequestURI(), e.fillInStackTrace());
        return WebResp.failure(ErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 处理自定义异常
     *
     * @param e 异常
     * @return WebResp
     */
    @ExceptionHandler(MyException.class)
    public WebResp<?> handleMyException(MyException e, HttpServletRequest request) {
        log.error("\u001B[31m请求：\u001B[34m{}\u001B[31m 发生自定义异常，异常信息：\u001B[0m{}\n详细信息：{}",
                request.getRequestURI(), e.getMessage(), e.getDetail(), e.fillInStackTrace());
        return WebResp.failure(e.getErrorCode(), e.getDetail());
    }

}
