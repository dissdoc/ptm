package org.phototimemachine.web.controller;

import org.apache.commons.io.FileUtils;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Avatar;
import org.phototimemachine.domain.Group;
import org.phototimemachine.domain.UserGroup;
import org.phototimemachine.service.*;
import org.phototimemachine.web.util.Numerical;
import org.phototimemachine.web.util.image.CustomImageMagick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RequestMapping("/user")
@Controller
public class AppUserController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private AssortmentService assortmentService;

    @Autowired
    private FaveService faveService;

    @Autowired
    private UserGroupService userGroupService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/upload/avatar", method = RequestMethod.POST)
    public @ResponseBody String uploadAvatar(@RequestParam MultipartFile file, @RequestParam("w") int w,
                                             @RequestParam("x") int x, @RequestParam("y") int y,
                                             @RequestParam("width") int width, @RequestParam("height") int height) {
        AppUser user = userService.findAuthUser();
        Avatar avatar = avatarService.findByUser(user.getUserId());
        if (avatar != null) {
            try {
                File directory = new File("/avatar/" + avatar.getId());
                FileUtils.deleteDirectory(directory);
                directory.mkdir();
            } catch (Exception ex) { ex.printStackTrace(); }
        } else {
            avatar = new Avatar();
            avatar.setUser(user);
            avatarService.save(avatar);
            user.setAvatar(avatar);
        }


        CustomImageMagick customImageMagick = new CustomImageMagick(file, avatar.getId().toString());
        customImageMagick.setFormat();
        customImageMagick.setAvatarPath();
        customImageMagick.generate2(w, x, y, width, height);

        return avatar.getId().toString();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/photos", method = RequestMethod.GET)
    public String photos(Model uiModel) {
        AppUser user = userService.findAuthUser();
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", user);
        return "search.photos.wall";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/favorites", method = RequestMethod.GET)
    public String favorites(Model uiModel) {
        AppUser user = userService.findAuthUser();
        uiModel.addAttribute("user", user);
        return "search.favorites.wall";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/albums", method = RequestMethod.GET)
    public String albums(Model uiModel) {
        AppUser user = userService.findAuthUser();
        uiModel.addAttribute("albums", albumService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("avatar", avatarService.findByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("owner", true);
        uiModel.addAttribute("auth", user);
        return "albums.list";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/albums", method = RequestMethod.GET)
    public String albumsUser(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findById(id);
        AppUser auth = userService.findAuthUser();
        if (user == null || (auth != null && auth.getUserId().intValue() == user.getUserId().intValue()))
            return "redirect:/user/albums";
        uiModel.addAttribute("albums", albumService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("avatar", avatarService.findByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("owner", false);
        uiModel.addAttribute("auth", auth);
        return "albums.list";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    public String assortments(Model uiModel) {
        AppUser user = userService.findAuthUser();
        uiModel.addAttribute("albums", assortmentService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("owner", true);
        uiModel.addAttribute("auth", user);
        return "collections.list";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/collections", method = RequestMethod.GET)
    public String collectionUser(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findById(id);
        AppUser auth = userService.findAuthUser();
        if (user == null) return "redirect:/";
        if (auth != null && auth.getUserId().intValue() == user.getUserId().intValue()) return "redirect:/user/collections";
        uiModel.addAttribute("albums", assortmentService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("owner", true);
        uiModel.addAttribute("auth", auth);
        return "collections.list";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/groups", method = RequestMethod.GET)
    public String groupsUser(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findById(id);
        AppUser auth = userService.findAuthUser();
        if (user == null) return "redirect:/";
        if (auth != null && auth.getUserId().intValue() == user.getUserId().intValue()) return "redirect:/groups/list";

        Set<Group> managedGroups = new HashSet<Group>();
        for (UserGroup userGroup: userGroupService.findAllMembers(user.getUserId()))
            managedGroups.add(userGroup.getGroup());

        uiModel.addAttribute("groups", groupService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("managed_groups", managedGroups);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", auth);
        return "groups.list";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Model uiModel) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/";
        uiModel.addAttribute("photos", photoService.findByUser(user.getUserId()));
        uiModel.addAttribute("groups", groupService.findFullByUser(user.getUserId()));
        uiModel.addAttribute("faves", faveService.findByUser(user.getUserId()));
        uiModel.addAttribute("albums", albumService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("assortments", assortmentService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("avatar", avatarService.findByUser(user.getUserId()));
        uiModel.addAttribute("owner", true);
        return "user.profile";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model uiModel) {
        AppUser auth = userService.findAuthUser();
        AppUser user = userService.findById(id);
        if (user == null) return "redirect:/user/profile";
        if (auth != null && auth.getUserId().intValue() == id.intValue()) return "redirect:/user/profile";
        uiModel.addAttribute("photos", photoService.findByUserPublic(user.getUserId()));
        uiModel.addAttribute("groups", groupService.findFullByUser(user.getUserId()));
        uiModel.addAttribute("faves", faveService.findByUser(user.getUserId()));
        uiModel.addAttribute("albums", albumService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("assortments", assortmentService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", auth);
        uiModel.addAttribute("avatar", avatarService.findByUser(user.getUserId()));
        uiModel.addAttribute("owner", false);
        return "user.profile";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public String contacts(Model uiModel) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/";
        uiModel.addAttribute("auth", user);
        uiModel.addAttribute("user", user);
        return "contact.list";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/contacts", method = RequestMethod.GET)
    public String contactsUser(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findById(id);
        if (user == null) return "redirect:/";
        AppUser auth = userService.findAuthUser();
        if (auth != null && auth.getUserId().intValue() == user.getUserId().intValue()) return "redirect:/user/contacts";
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", auth);
        return "contact.list";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/photos", method = RequestMethod.GET)
    public String userPhotos(@PathVariable("id") Long id, Model uiModel) {
        AppUser auth = userService.findAuthUser();
        AppUser user = userService.findById(id);
        if (user == null) return "redirect:/";
        if (auth != null && auth.getUserId().intValue() == id.intValue()) return "redirect:/user/photos";
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", auth);
        return "search.photos.wall";
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/{id}/favorites", method = RequestMethod.GET)
    public String userFavorites(@PathVariable("id") Long id, Model uiModel) {
        AppUser auth = userService.findAuthUser();
        AppUser user = userService.findById(id);
        if (user == null) return "redirect:/";
        if (auth != null && auth.getUserId().intValue() == id.intValue()) return "redirect:/user/favorites";
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", auth);
        return "search.favorites.wall";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/about", method = RequestMethod.POST)
    public @ResponseBody String updateAbout(@RequestParam("field") String field) {
        AppUser user = userService.findAuthUser();
        user.setAbout(field);
        userService.save(user);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/hometown", method = RequestMethod.POST)
    public @ResponseBody String updateHomeTown(@RequestParam("address") String address,
                                               @RequestParam("lat") String lat, @RequestParam("lng") String lng) {
        AppUser user = userService.findAuthUser();
        user.setHomeTown(address);
        user.sethLatitude(lat);
        user.sethLongitude(lng);
        userService.save(user);
        return address;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/currentcity", method = RequestMethod.POST)
    public @ResponseBody String updateCurrentCity(@RequestParam("address") String address,
                                                  @RequestParam("lat") String lat, @RequestParam("lng") String lng) {
        AppUser user = userService.findAuthUser();
        user.setCurrentCity(address);
        user.setcLatitude(lat);
        user.setcLongitude(lng);
        userService.save(user);
        return address;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/name", method = RequestMethod.POST)
    public @ResponseBody String updateName(@RequestParam("name") String name) {
        AppUser user = userService.findAuthUser();
        if (name == null || name.trim().length() <= 0) return user.fullName();
        String[] array = name.split(" ");
        if (array.length <= 1) return user.fullName();
        user.setFirstName(array[0]);
        user.setLastName(array[1]);
        userService.save(user);
        return user.fullName();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/date", method = RequestMethod.POST)
    public @ResponseBody String updateDate(@RequestParam("d") String d) {
        AppUser user = userService.findAuthUser();
        if (d == null || d.trim().length() <= 0) {
            user.setDay(null);
            user.setMonth(null);
            user.setYear(null);
            userService.save(user);
        } else {
            String[] array = d.split(" ");
            if (array.length <= 2) return user.formatDate();
            user.setDay(Numerical.getInt(array[0]));
            user.setMonth(Numerical.getInt(array[1]));
            user.setYear(Numerical.getInt(array[2]));
            userService.save(user);
        }
        return user.formatDate();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/update/gender", method = RequestMethod.POST)
    public @ResponseBody String updateGender(@RequestParam("gender") Boolean gender) {
        AppUser user = userService.findAuthUser();
        user.setGender(gender);
        userService.save(user);
        return user.gender();
    }
}
