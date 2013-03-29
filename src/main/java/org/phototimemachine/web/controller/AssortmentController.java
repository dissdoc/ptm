package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Assortment;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.AssortmentService;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/collections")
@Controller
public class AssortmentController {

    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @Autowired
    private AssortmentService assortmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String add(@RequestParam("id") Long photo_id, @RequestParam("album") Long album_id) {
        if (album_id.intValue() <= 0 || photo_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";
        Assortment assortment = user.getAssortmentById(album_id);
        Photo photo = photoService.findById(photo_id);
        assortment.getPhotos().add(photo);
        if (assortment.getThumbnail() == null) assortment.setThumbnail(photo);
        assortmentService.save(assortment);
        new Connector().addToModel("assortment", photo_id, album_id);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id") Long photo_id, @RequestParam("album") Long album_id) {
        if (album_id.intValue() <= 0 || photo_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";
        Assortment assortment = user.getAssortmentById(album_id);
        Photo photo = photoService.findById(photo_id);
        assortment.getPhotos().remove(photo);

        if (assortment.getThumbnail().getId().intValue() == photo_id.intValue())
            assortment.setThumbnail(assortment.getPhotos().size() > 0 ? assortment.getPhotos().iterator().next() : null);

        assortmentService.save(assortment);
        new Connector().removeFromModel("assortment", photo_id, album_id);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestParam("name") String name,
                                       @RequestParam("description") String description, @RequestParam("id") Long id) {
        if (id.intValue() <= 0 || name.trim().length() <= 0) return "";
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(id);
        if (user.getUserId().intValue() == photo.getAppUser().getUserId().intValue()) return "";

        Assortment assortment = new Assortment();
        assortment.setName(name);
        assortment.setDescription(description);
        assortment.setAppUser(user);
        assortment.getPhotos().add(photo);
        assortment.setThumbnail(photo);
        assortmentService.save(assortment);
        new Connector().addToModel("assortment", id, assortment.getId());
        user.addAssortment(assortment);

        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public @ResponseBody String newAssortment(@RequestParam("name") String name, @RequestParam("description") String description) {
        AppUser user = userService.findAuthUser();
        if (user == null) return null;
        Assortment assortment = new Assortment();
        assortment.setName(name);
        assortment.setDescription(description);
        assortment.setAppUser(user);
        assortmentService.save(assortment);
        return "OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public @ResponseBody String all(@RequestParam("id") Long id) {
        if (id.intValue() <= 0) return "";
        AppUser appUser = userService.findAuthUser();
        if (appUser == null) return "";

        String json = "";
        for (Assortment assortment: appUser.getAssortments())
            json += "{\"name\": \""+assortment.getName()+"\", \"id\": \""+assortment.getId()+"\", \"exist\": \""+
                    assortment.isExistPhoto(id)+"\"},";
        if (json.length() > 0) json = json.substring(0, json.length()-1);
        return "{\"GroupList\":[" + json + "]}";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/collection/{id}", method = RequestMethod.GET)
    public String assortment(@PathVariable("id") Long id, Model uiModel) {
        if (id.intValue() <= 0) return "redirect:/users/collections";
        Assortment assortment = assortmentService.findById(id);
        uiModel.addAttribute("album", assortment);
        uiModel.addAttribute("photos", assortment.getPhotos());
        uiModel.addAttribute("user", assortment.getAppUser());
        uiModel.addAttribute("auth", userService.findAuthUser());
        return "collection.photos";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody String deleteAssortment(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        Assortment assortment = assortmentService.findById(id);
        if (user.getUserId().intValue() == assortment.getAppUser().getUserId().intValue()) {
            Connector connector = new Connector();
            for (Photo photo :assortment.getPhotos())
                connector.removeFromModel("assortment", photo.getId(), id);
            assortmentService.delete(assortment);
        }
        return "200 Ok!";
    }
}
