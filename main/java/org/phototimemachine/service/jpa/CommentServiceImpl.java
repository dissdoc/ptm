package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Comment;
import org.phototimemachine.repository.CommentRepository;
import org.phototimemachine.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("commentService")
@Repository
@Transactional
public class CommentServiceImpl implements CommentService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Comment findById(Long id) {
        em.clear();
        TypedQuery<Comment> query = em.createNamedQuery("Comment.findById", Comment.class);
        query.setParameter("id", id);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByPhotoId(Long photo_id) {
        em.clear();
        TypedQuery<Comment> query = em.createNamedQuery("Comment.findByPhotoId", Comment.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByUserId(Long user_id) {
        em.clear();
        TypedQuery<Comment> query = em.createNamedQuery("Comment.findByUserId", Comment.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}
