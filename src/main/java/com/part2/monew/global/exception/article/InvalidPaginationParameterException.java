package com.part2.monew.global.exception.article;

import com.part2.monew.global.exception.BusinessException;
import com.part2.monew.global.exception.ErrorCode;

public class InvalidPaginationParameterException extends BusinessException {

    public InvalidPaginationParameterException() {
        super(ErrorCode.INVALID_PAGINATION_PARAMETER);
    }
} 