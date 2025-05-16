package exception_handler.exception;

import exception_handler.entity.ErrorCode;

import java.util.Map;

public class ResourceNotFoundException  extends MyException {

    public ResourceNotFoundException(Map<String, Object> errorDetail) {
        super(ErrorCode.RESOURCE_NOT_FOUND, errorDetail);
    }

    public ResourceNotFoundException(Map<String, Object> errorDetail, Throwable cause) {
        super(ErrorCode.RESOURCE_NOT_FOUND, errorDetail, cause);
    }

}
