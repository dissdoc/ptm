package org.phototimemachine.web.controller;

import org.phototimemachine.domain.*;
import org.phototimemachine.searcher.Connector;
import org.phototimemachine.service.*;
import org.phototimemachine.web.util.Numerical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequestMapping("/groups")
@Controller
public class GroupController {

    private static Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private PhotoGroupService photoGroupService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private TopicService topicService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createForm() {
        return "group.create";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/groups/list";

        Group group = new Group();
        group.setAuthor(user);
        group.setClosed(Numerical.getBool(request.getParameter("closed")));
        group.setMarked(Numerical.getBool(request.getParameter("marked")));
        group.setName(request.getParameter("name"));
        group.setDescription(request.getParameter("description"));
        group.setRules(request.getParameter("rules"));
        group.setDeleted(Boolean.FALSE);
        groupService.save(group);
        user.addGroup(group);

        return "redirect:/groups/list";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (user.getUserId().intValue() != group.getAuthor().getUserId().intValue())
            return "redirect:/groups/" + id;
        uiModel.addAttribute("group", group);
        return "group.edit";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String edit(@PathVariable("id") Long id, HttpServletRequest request) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (user.getUserId().intValue() == group.getAuthor().getUserId().intValue()) {
            group.setClosed(Numerical.getBool(request.getParameter("closed")));
            group.setMarked(Numerical.getBool(request.getParameter("marked")));
            group.setName(request.getParameter("name"));
            group.setDescription(request.getParameter("description"));
            group.setRules(request.getParameter("rules"));
            groupService.save(group);
        }

        return "redirect:/groups/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model uiModel) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/";

        Set<Group> managedGroups = new HashSet<Group>();
        for (UserGroup userGroup: userGroupService.findAllMembers(user.getUserId()))
            managedGroups.add(userGroup.getGroup());

        uiModel.addAttribute("groups", groupService.findAllByUser(user.getUserId()));
        uiModel.addAttribute("managed_groups", managedGroups);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("auth", user);
        return "groups.list";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String detail(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/groups";

        Group group = groupService.findById(id);
        boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue();
        boolean join = userService.isJoinGroup(user.getUserId(), id);
        boolean waiting = userService.isWaitGroup(user.getUserId(), id);

        uiModel.addAttribute("user_join", join);
        uiModel.addAttribute("user_waiting", waiting);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("owner", owner);
        uiModel.addAttribute("group", group);
        uiModel.addAttribute("photos", photoService.findAgreeGroup(id));
        uiModel.addAttribute("topic_count", topicService.countByGroup(id));
        uiModel.addAttribute("topics", topicService.findAllByGroup(id, 5));
        return "group.photos";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getAuthor().getUserId().intValue() == user.getUserId().intValue()) {
            group.setDeleted(Boolean.TRUE);
            groupService.save(group);
        }

        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/restore", method = RequestMethod.POST)
    public @ResponseBody String restore(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getAuthor().getUserId().intValue() == user.getUserId().intValue()) {
            group.setDeleted(Boolean.FALSE);
            groupService.save(group);
        }
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/admin", method = RequestMethod.GET)
    public String admin(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getAuthor().getUserId().intValue() != user.getUserId().intValue())
            return "redirect:/groups"+id;
        uiModel.addAttribute("photos", photoService.findNotAgreeGroup(id));
        uiModel.addAttribute("group", group);

        return "group.waiting.photos";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/admin/apply", method = RequestMethod.POST)
    public @ResponseBody String applyPhoto(@PathVariable("id") Long id, @RequestParam("photo_id") Long photo_id) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getAuthor().getUserId().intValue() != user.getUserId().intValue()) return "";

        PhotoGroup photoGroup = photoGroupService.findByPhotoAndGroup(photo_id, id);
        photoGroup.setAgreed(Boolean.TRUE);
        photoGroupService.save(photoGroup);
        new Connector().addToModel("group", photo_id, id);
        UserGroup userGroup = userGroupService.findByIds(id, photoGroup.getAppUser().getUserId());
        if (userGroup.getAgreed() != 1) {
            userGroup.setAgreed((byte) 1);
            userGroupService.save(userGroup);
        }

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/admin/remove", method = RequestMethod.POST)
    public @ResponseBody String removePhoto(@PathVariable("id") Long id, @RequestParam("photo_id") Long photo_id) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getAuthor().getUserId().intValue() != user.getUserId().intValue()) return "";

        PhotoGroup photoGroup = photoGroupService.findByPhotoAndGroup(photo_id, id);
        photoGroupService.delete(photoGroup);
        new Connector().removeFromModel("group", photo_id, id);

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/upload")
    public String uploadPhotos(@PathVariable("id") Long id, Model uiModel) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findByIdDetails(id);
        boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue();
        boolean join = userService.isJoinGroup(user.getUserId(), id);
        boolean waiting = userService.isWaitGroup(user.getUserId(), id);
        if (!join && !owner && !waiting) return "redirect:/groups/" + id;

        Set<Photo> photos = new HashSet<Photo>();
        for (Photo photo: user.getPhotos()) {
            if (group.getPhotos().contains(photo)) continue;
            photos.add(photo);
        }

        uiModel.addAttribute("group", group);
        uiModel.addAttribute("photos", photos);

        return "group.load.photos";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/join", method = RequestMethod.POST)
    public @ResponseBody String join(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group.getClosed()) return "";

        UserGroup ug = new UserGroup();
        ug.setGroup(group);
        ug.setAppUser(user);
        ug.setAgreed((byte) 1);
        userGroupService.save(ug);

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/apply", method = RequestMethod.POST)
    public @ResponseBody String apply(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        if (userService.isWaitGroup(user.getUserId(), id)) return "redirect:/groups/"+id;
        Group group = groupService.findById(id);
        if (!group.getClosed()) return "";

        UserGroup ug = new UserGroup();
        ug.setGroup(group);
        ug.setAppUser(user);
        ug.setAgreed((byte) 0);
        userGroupService.save(ug);

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.POST)
    public @ResponseBody String leave(@PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);

        UserGroup ug = userGroupService.findByIds(group.getId(), user.getUserId());
        if (ug == null) return "";
        userGroupService.delete(ug);

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/topic/{topic_id}", method = RequestMethod.GET)
    public String showTopicDetail(Model uiModel, @PathVariable("id") Long id,
                                  @PathVariable("topic_id") Long topic_id) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/groups/"+id;

        Group group = groupService.findById(id);
        Boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue() ?
                Boolean.TRUE : Boolean.FALSE;
        boolean join = userService.isJoinGroup(user.getUserId(), id);
        Topic topic = topicService.findById(topic_id);

        uiModel.addAttribute("owner", owner);
        uiModel.addAttribute("group", group);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("user_join", join);
        uiModel.addAttribute("topics", topicService.findAllByGroup(id));
        uiModel.addAttribute("topic", topic);

        return "topic.detail";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{id}/topics", method = RequestMethod.GET)
    public String currentOnes(Model uiModel, @PathVariable("id") Long id) {
        AppUser user = userService.findAuthUser();
        if (user == null) return "redirect:/groups";

        Group group = groupService.findById(id);
        boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue();
        boolean join = userService.isJoinGroup(user.getUserId(), id);

        uiModel.addAttribute("user_join", join);
        uiModel.addAttribute("owner", owner);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("group", group);
        uiModel.addAttribute("topics", topicService.findAllByGroup(id));
        return "topics.all";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/all", method = RequestMethod.POST)
    public @ResponseBody String all(@RequestParam("id") Long id) {
        if (id.intValue() <= 0) return "";
        AppUser appUser = userService.findAuthUser();
        if (appUser == null) return "";

        String json = "";
        for (Group group: groupService.findFullByUser(appUser.getUserId()))
            json += "{\"name\": \""+group.getName()+"\", \"id\": \""+group.getId()+"\", \"exist\": \""+
                    group.isExistPhoto(id)+"\"},";

        if (json.length() > 0) json = json.substring(0, json.length()-1);
        return "{\"GroupList\":[" + json + "]}";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String add(@RequestParam("id") Long photo_id, @RequestParam("group") Long group_id) {
        if (group_id.intValue() <= 0 || photo_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";

        Group group = groupService.findById(group_id);
        if (group == null) return "";

        boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue();
        boolean join = userService.isJoinGroup(user.getUserId(), group_id);
        boolean waiting = userService.isWaitGroup(user.getUserId(), group_id);
        if (!owner && !join && !waiting) return "";

        Photo photo = photoService.findById(photo_id);
        PhotoGroup photoGroup = new PhotoGroup();
        photoGroup.setGroup(group);
        photoGroup.setPhoto(photo);
        photoGroup.setAppUser(user);
        if (owner || join) {
            photoGroup.setAgreed(Boolean.TRUE);
            new Connector().addToModel("group", photo_id, group_id);
        }
        else if (waiting) photoGroup.setAgreed(Boolean.FALSE);
        photoGroupService.save(photoGroup);
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add_to_group", method = RequestMethod.POST)
    public @ResponseBody String addToGroup(@RequestParam("group") Long group_id, HttpServletRequest request) {
        String[] ids = request.getParameterValues("ids[]");
        if (ids.length <= 0 || group_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";

        Group group = groupService.findById(group_id);
        if (group == null) return "";

        boolean owner = group.getAuthor().getUserId().intValue() == user.getUserId().intValue();
        boolean join = userService.isJoinGroup(user.getUserId(), group_id);
        boolean waiting = userService.isWaitGroup(user.getUserId(), group_id);
        if (!owner && !join && !waiting) return "";

        for (String item: ids) {
            Long photo_id = new Long(item);
            Photo photo = photoService.findById(photo_id);
            PhotoGroup photoGroup = new PhotoGroup();
            photoGroup.setGroup(group);
            photoGroup.setPhoto(photo);
            photoGroup.setAppUser(user);
            if (owner || join) {
                photoGroup.setAgreed(Boolean.TRUE);
                new Connector().addToModel("group", photo_id, group_id);
            }
            else if (waiting) photoGroup.setAgreed(Boolean.FALSE);
            photoGroupService.save(photoGroup);

            photo.addPhotoGroup(photoGroup);
            user.addPhotoGroup(photoGroup);
            photoService.save(photo);
        }

        return "200 OK!";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id") Long photo_id, @RequestParam("group") Long group_id) {
        if (group_id.intValue() <= 0 || photo_id.intValue() <= 0) return "";
        AppUser user = userService.findAuthUser();
        if (user == null) return "";

        Group group = groupService.findById(group_id);
        if (group == null) return "";

        PhotoGroup photoGroup = photoGroupService.findByPhotoAndGroup(photo_id, group_id);
        photoGroupService.delete(photoGroup);
        new Connector().removeFromModel("group", photo_id, group_id);
        return "";
    }
}
