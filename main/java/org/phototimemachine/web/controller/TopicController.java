package org.phototimemachine.web.controller;

import org.phototimemachine.common.TopicJson;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Group;
import org.phototimemachine.domain.Topic;
import org.phototimemachine.service.GroupService;
import org.phototimemachine.service.TopicService;
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

@RequestMapping("/topics")
@Controller
public class TopicController {

    private static Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private GroupService groupService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody TopicJson create(@RequestParam("id") Long id, @RequestParam("name") String name,
                                       @RequestParam("description") String description) {
        if (id.intValue() <= 0) return null;
        AppUser user = userService.findAuthUser();
        Group group = groupService.findById(id);
        if (group == null) return null;

        Topic topic = new Topic();
        topic.setAuthor(user);
        topic.setGroup(group);
        topic.setDescription(description);
        topic.setTheme(name);
        topicService.save(topic);

        TopicJson topicJson = new TopicJson();
        topicJson.setName(topic.getTheme());
        topicJson.setDescription(topic.getDescription());
        topicJson.setUserName(user.fullName());
        topicJson.setDate(topic.getDateAgo());
        topicJson.setId(topic.getId());
        topicJson.setReplies(topic.getReplies().size());

        return topicJson;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id") Long id, @RequestParam("group_id") Long group_id) {
        if (id.intValue() <= 0 || group_id.intValue() <= 0) return "";
        Topic topic = topicService.findById(id);
        topicService.delete(topic);
        return "200 OK!";
    }
}
