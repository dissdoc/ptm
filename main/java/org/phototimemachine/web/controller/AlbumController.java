package org.phototimemachine.web.controller;

import org.phototimemachine.domain.Album;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.AlbumService;
import org.phototimemachine.service.PhotoService;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/albums")
@Controller
public class AlbumController {

    private static final Logger logger = LoggerFactory.getLogger(AlbumController.class);

    @Autowired
    private AlbumService albumService;

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
        Album album = user.getAlbumById(album_id);
        Photo photo = photoService.findById(photo_id);
        album.getPhotos().add(photo);
        if (album.getThumbnail() == null) album.setThumbnail(photo);
        albumService.save(album);
        photoService.save(photo);
        new Connector().addToModel("album", photo_id, album_id);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id") Long photo_id, @RequestParam("album") Long album_id) {
        if (album_id.intValue() <= 0 || photo_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";
        Album album = user.getAlbumById(album_id);
        Photo photo = photoService.findById(photo_id);
        album.getPhotos().remove(photo);

        if (album.getThumbnail().getId().intValue() == photo_id.intValue())
            album.setThumbnail(album.getPhotos().size() > 0 ? album.getPhotos().iterator().next() : null);

        albumService.save(album);
        photoService.save(photo);
        new Connector().removeFromModel("album", photo_id, album_id);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public @ResponseBody String newAlbum(@RequestParam("name") String name, @RequestParam("description") String description) {
        AppUser user = userService.findAuthUser();
        if (user == null) return null;
        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setAppUser(user);
        albumService.save(album);
        return "OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestParam("name") String name,
                 @RequestParam("description") String description, @RequestParam("id") Long id) {
        if (id.intValue() <= 0 || name.trim().length() <= 0) return "";
        AppUser user = userService.findAuthUser();
        Photo photo = photoService.findById(id);
        if (user.getUserId().intValue() != photo.getAppUser().getUserId().intValue()) return "";

        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setAppUser(user);
        album.getPhotos().add(photo);
        album.setThumbnail(photo);
        albumService.save(album);
        new Connector().addToModel("album", id, album.getId());
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public @ResponseBody String all(@RequestParam("id") Long id) {
        if (id.intValue() <= 0) return "";
        AppUser appUser = userService.findAuthUser();
        if (appUser == null) return "";

        String json = "";
        for (Album album: albumService.findAllByUser(appUser.getUserId()))
            json += "{\"name\": \""+album.getName()+"\", \"id\": \""+album.getId()+"\", \"exist\": \""+
                    album.isExistPhoto(id)+"\"},";
        if (json.length() > 0) json = json.substring(0, json.length()-1);
        return "{\"GroupList\":[" + json + "]}";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/album/{id}", method = RequestMethod.GET)
    public String album(@PathVariable("id") Long id, Model uiModel) {
        if (id.intValue() <= 0) return "redirect:/users/albums";
        Album album = albumService.findById(id);
        uiModel.addAttribute("album", album);
        uiModel.addAttribute("photos", album.getPhotos());
        uiModel.addAttribute("user", album.getAppUser());
        uiModel.addAttribute("auth", userService.findAuthUser());
        return "album.photos";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody String deleteAlbum(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        Album album = albumService.findById(id);
        if (user.getUserId().intValue() == album.getAppUser().getUserId().intValue()) {
            Connector connector = new Connector();
            for (Photo photo: album.getPhotos())
                connector.removeFromModel("album", photo.getId(), id);
            albumService.delete(album);
        }
        return "200 Ok!";
    }
}
