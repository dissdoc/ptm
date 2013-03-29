package org.phototimemachine.service.validator.email;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("emailValidator")
public class EmailValidator implements Validator {

    final Logger logger = LoggerFactory.getLogger(EmailValidator.class);

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return AppUser.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AppUser appUser = (AppUser) o;
        if (userService.isExistEmail(appUser.getEmail()))
            errors.rejectValue("email", "validation.email.Exists.message");
    }
}
