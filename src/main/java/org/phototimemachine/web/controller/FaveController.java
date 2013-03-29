package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Fave;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.FaveService;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/faves")
@Controller
public class FaveController {

    private static final Logger logger = LoggerFactory.getLogger(FaveController.class);

    @Autowired
    private FaveService faveService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/fave", method = RequestMethod.POST)
    public @ResponseBody String fave(@RequestParam("user_id") Long user_id, @RequestParam("photo_id") Long photo_id) {
        Fave fave = faveService.findByUserAndPhoto(user_id, photo_id);
        if (fave == null) {
            AppUser user = userService.findById(user_id);
            Photo photo = photoService.findById(photo_id);
            if (user != null && photo != null && photo.getPrivacy() == 0) {
                fave = new Fave();
                fave.setAppUser(user);
                fave.setPhoto(photo);
                faveService.save(fave);
                new Connector().addToModel("fave", photo_id, user_id);
            }
        }
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/unfave", method = RequestMethod.POST)
    public @ResponseBody String unfave(@RequestParam("user_id") Long user_id, @RequestParam("photo_id") Long photo_id) {
        Fave fave = faveService.findByUserAndPhoto(user_id, photo_id);
        if (fave != null) {
            faveService.delete(fave);
            new Connector().removeFromModel("fave", photo_id, user_id);
        }
        return "";
    }
}
