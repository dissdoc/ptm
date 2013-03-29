package org.phototimemachine.web.controller;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.domain.Comment;
import org.phototimemachine.domain.Photo;
import org.phototimemachine.service.CommentService;
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
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/comments")
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(Model uiModel, @RequestParam("message") String message,
                        @RequestParam("photo_id") Long photo_id) {
        Photo photo = photoService.findById(photo_id);
        AppUser user = userService.findAuthUser();
        Comment comment = new Comment();
        comment.setDescription(message);
        comment.setPhoto(photo);
        comment.setAppUser(user);
        commentService.save(comment);

        uiModel.addAttribute("photo", photo);
        uiModel.addAttribute("user", user);
        uiModel.addAttribute("comments", commentService.findByPhotoId(photo_id));

        return "photo.comments";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody String delete(@RequestParam("comment_id") Long comment_id) {
        Comment comment = commentService.findById(comment_id);
        commentService.delete(comment);
        return "200 OK!";
    }
}
