package com.ming.blog.config;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.internal.constraintvalidators.AbstractEmailValidator;

public class EmailValidator extends AbstractEmailValidator<Email> {
    public EmailValidator() {
    }
}
