package com.xbf8.secure_s3.file.validate;

import com.xbf8.secure_s3.SS3Properties;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileSizeValidator implements ConstraintValidator<SS3FileSize, Long> {
    private final Long min;
    private final Long max;
    public FileSizeValidator(SS3Properties ss3Properties) {
        this.min = ss3Properties.validate().file().minSizeBytes();
        this.max = ss3Properties.validate().file().maxSizeBytes();
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null){
            return true;
        }
        return value >= min && value <= max;
    }
}
