package exception_handler.exception;

import exception_handler.entity.ErrorCode;

import java.util.Map;

public class InternalServerErrorException extends MyException {

    public InternalServerErrorException(Map<String, Object> errorDetail) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, errorDetail);
    }

}
