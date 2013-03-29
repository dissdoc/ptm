package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Reply;
import org.phototimemachine.repository.ReplyRepository;
import org.phototimemachine.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service("replyService")
@Repository
@Transactional
public class ReplyServiceImpl implements ReplyService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ReplyRepository replyRepository;

    @Override
    public Reply findById(Long id) {
        return replyRepository.findOne(id);
    }

    @Override
    public Reply save(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public void delete(Reply reply) {
        replyRepository.delete(reply);
    }
}
