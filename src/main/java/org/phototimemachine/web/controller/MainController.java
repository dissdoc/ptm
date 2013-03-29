package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.TagService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private PhotoService photoService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model uiModel) {
        AppUser user = userService.findAuthUser();
        uiModel.addAttribute("user", user != null ? user.getUserId() : 0);
        return "main";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/tour", method = RequestMethod.GET)
    public String tour() {
        logger.info("Enter to tour page");
        return "tour";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/about_us", method = RequestMethod.GET)
    public String about_us() {
        logger.info("Enter to about us page");
        return "about_us";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/faq", method = RequestMethod.GET)
    public String faq() {
        logger.info("Enter to faq page");
        return "faq";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/rules", method = RequestMethod.GET)
    public String rules() {
        logger.info("Enter to rules page");
        return "rules";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/donate", method = RequestMethod.GET)
    public String donate() {
        logger.info("Enter to donate page");
        return "donate";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contact() {
        return "contact";
    }
}
