package com.part2.monew.global.exception.article;

import com.part2.monew.global.exception.BusinessException;
import com.part2.monew.global.exception.ErrorCode;

public class InvalidDateRangeException extends BusinessException {

    public InvalidDateRangeException() {
        super(ErrorCode.INVALID_DATE_RANGE);
    }
} 