package com.xbf8.secure_s3.file.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface SS3FileSize {
    String message() default "Invalid file size";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
