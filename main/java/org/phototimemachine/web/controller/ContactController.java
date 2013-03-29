package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * User: dissdoc
 * Date: 14.03.13
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping("/contacts")
@Controller
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addContact(Model uiModel,
                             @RequestParam("user_id") Long user_id,
                             @RequestParam(value = "photo_id", required = false) Long photo_id) {
        AppUser user = userService.findAuthUser();
        AppUser contact = userService.findById(user_id);
        user.getContacts().add(contact);
        userService.save(user);
        if (photo_id != null && photo_id.intValue() > 0) {
            uiModel.addAttribute("user", user);
            uiModel.addAttribute("photo", photoService.findById(photo_id));
            return "user.status";
        } else {
            uiModel.addAttribute("auth", user);
            uiModel.addAttribute("user", contact);
            return "user.display";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteContact(Model uiModel,
                                @RequestParam("user_id") Long user_id,
                                @RequestParam(value = "photo_id", required = false) Long photo_id) {
        AppUser user = userService.findAuthUser();
        AppUser contact = userService.findById(user_id);
        user.getContacts().remove(contact);
        userService.save(user);
        if (photo_id != null && photo_id.intValue() > 0) {
            uiModel.addAttribute("user", user);
            uiModel.addAttribute("photo", photoService.findById(photo_id));
            return "user.status";
        } else {
            uiModel.addAttribute("auth", user);
            uiModel.addAttribute("user", contact);
            return "user.display";
        }
    }
}
