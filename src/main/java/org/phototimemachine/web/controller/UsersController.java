package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Role;
import org.phototimemachine.searcher.RecommendGenerator;
import org.phototimemachine.service.RoleService;
import org.phototimemachine.service.UserService;
import org.phototimemachine.service.validator.email.CustomEmailSender;
import org.phototimemachine.service.validator.email.EmailCorrectValid;
import org.phototimemachine.service.validator.email.EmailValidator;
import org.phototimemachine.service.validator.password.PasswordGenerator;
import org.phototimemachine.service.validator.password.PasswordValidator;
import org.phototimemachine.web.form.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@RequestMapping("/users")
@Controller
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private CustomEmailSender customEmailSender;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private EmailCorrectValid emailCorrectValid;

    @Autowired
    private EmailValidator emailValidator;

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid AppUser appUser, BindingResult result, Model uiModel, HttpServletRequest request,
                         RedirectAttributes attributes, Locale locale) {
        logger.info("Show action register appUser");
        passwordValidator.validate(appUser, result);
        emailValidator.validate(appUser, result);

        if (result.hasErrors()) {
            uiModel.addAttribute("message", new Message("error",
                    messageSource.getMessage("user_save_fail", new Object[] {}, locale)));
            uiModel.addAttribute("user", appUser);
            return "users/register";
        }

        uiModel.asMap().clear();
        attributes.addFlashAttribute("message", new Message("success",
                messageSource.getMessage("user_save_success", new Object[]{}, locale)));

        Role role = roleService.findById("ROLE_USER");
        appUser.setRole(role);

        PasswordGenerator pg = new PasswordGenerator(appUser.getFirstName(), appUser.getLastName(),
                appUser.getEmail(), appUser.getPassword());
        appUser.setPassword(pg.getPassword());

        userService.save(appUser);
        customEmailSender.sendRegister(appUser.getEmail(), locale);
        return "redirect:/";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(Model uiModel) {
        logger.info("Enter to register page");
        AppUser appUser = new AppUser();
        uiModel.addAttribute("appUser", appUser);
        return "users/register";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/denied", method = RequestMethod.GET)
    public String denied() {
        return "users/denied";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        logger.info("Login page");
        return "users/login";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/loginfail", method = RequestMethod.GET)
    public String loginFail(Model uiModel, Locale locale) {
        logger.info("Login failed detected");
        uiModel.addAttribute("message", new Message("error",
                messageSource.getMessage("user_login_fail", new Object[]{}, locale)));
        return "users/login";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public String forgotForm(Model uiModel, Locale locale) {
        logger.info("Forgot password page");
        return "users/forgot";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public String forgot(@RequestParam(value = "email", required = false) String email, Model uiModel, Locale locale) {
        logger.info("Forgot password POST method");
        if (!emailCorrectValid.validate(email) || !(userService.isExistEmail(email))) {
            uiModel.addAttribute("message", new Message("error",
                    messageSource.getMessage("forgot_email_fail",new Object[]{}, locale)));
            return "users/forgot";
        }

        AppUser appUser = userService.findByEmail(email);
        PasswordGenerator pg = new PasswordGenerator();
        String newPassword = pg.generatePassword(8);
        pg = new PasswordGenerator(appUser.getFirstName(), appUser.getLastName(),
                appUser.getEmail(), newPassword);
        appUser.setPassword(pg.getPassword());
        userService.save(appUser);
        customEmailSender.restorePassword(email, newPassword, locale);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/alarms/count", method = RequestMethod.POST)
    public @ResponseBody String alarmsCount() {
        AppUser user = userService.findAuthUser();
        RecommendGenerator rg = new RecommendGenerator();
        return rg.newRecommends(user)+"";
    }
}
