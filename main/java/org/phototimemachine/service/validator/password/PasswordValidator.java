package org.phototimemachine.service.validator.password;

import org.phototimemachine.domain.AppUser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("passwordValidator")
public class PasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return AppUser.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AppUser appUser = (AppUser) o;
        if (!appUser.getPassword().equals(appUser.getConfirmPassword()))
            errors.rejectValue("confirmPassword", "validation.confirmPassword.not_equals");
    }
}
