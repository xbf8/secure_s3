package com.xbf8.secure_s3.file.validate;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileExtensionValidator implements ConstraintValidator<SS3FileExtension, String> {
    @Value("${secure_s3.validate.file.extensions}")
    private String extensions;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        List<String> extensionList = Arrays.asList(extensions.split(","));
        return extensionList.contains(value);
    }
}
