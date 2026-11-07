package com.xbf8.secure_s3.file.validate;

import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileSizeValidator implements ConstraintValidator<SS3FileSize, Long> {
    @Value("${secure_s3.validate.file.min-size}")
    private Long min;

    @Value("${secure_s3.validate.file.max-size}")
    private Long max;

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null){
            return true;
        }
        return value >= min && value <= max;
    }
}
