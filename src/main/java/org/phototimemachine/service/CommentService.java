package org.phototimemachine.service;

import org.phototimemachine.domain.Comment;

import java.util.List;

public interface CommentService {

    public Comment findById(Long id);
    public List<Comment> findByPhotoId(Long photo_id);
    public List<Comment> findByUserId(Long user_id);
    public Comment save(Comment comment);
    public void delete(Comment comment);
}
