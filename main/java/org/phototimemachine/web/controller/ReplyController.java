package org.phototimemachine.web.controller;

import org.phototimemachine.common.ReplyJson;
import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Reply;
import org.phototimemachine.domain.Topic;
import org.phototimemachine.service.GroupService;
import org.phototimemachine.service.ReplyService;
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

@RequestMapping("/replies")
@Controller
public class ReplyController {

    private static Logger logger = LoggerFactory.getLogger(ReplyController.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReplyService replyService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody ReplyJson add(@RequestParam("topic_id") Long topic_id, @RequestParam("comment") String comment) {
        AppUser user = userService.findAuthUser();
        Topic topic = topicService.findById(topic_id);
        if (topic == null) return null;

        Reply reply = new Reply();
        reply.setTopic(topic);
        reply.setDescription(comment);
        reply.setAuthor(user);
        replyService.save(reply);
        topic.addReply(reply);
        user.addReply(reply);

        ReplyJson replyJson = new ReplyJson();
        replyJson.setDescription(reply.getDescription());
        replyJson.setDate(reply.getDateAgo());
        replyJson.setUserName(user.fullName());
        replyJson.setId(reply.getId());

        return replyJson;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("id") Long id) {
        if (id.intValue() <= 0) return "";
        Reply reply = replyService.findById(id);
        replyService.delete(reply);
        return "200 OK!";
    }
}
