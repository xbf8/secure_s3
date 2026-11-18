package com.xbf8.secure_s3.file.validate;

import java.util.List;

import com.xbf8.secure_s3.SS3Properties;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileExtensionValidator implements ConstraintValidator<SS3FileExtension, String> {
    private final List<String> extensions;
    public FileExtensionValidator(SS3Properties ss3Properties) {
        this.extensions = ss3Properties.validate().file().getExtensionsList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return this.extensions.contains(value);
    }
}
