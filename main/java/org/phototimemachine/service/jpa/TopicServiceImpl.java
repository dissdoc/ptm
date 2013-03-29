package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Topic;
import org.phototimemachine.repository.TopicRepository;
import org.phototimemachine.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("topicService")
@Repository
@Transactional
public class TopicServiceImpl implements TopicService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TopicRepository topicRepository;

    @Override
    @Transactional(readOnly = true)
    public Topic findById(Long id) {
        return topicRepository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> findAllByGroup(Long group_id) {
        TypedQuery<Topic> query = em.createNamedQuery("Topic.findAllByGroup", Topic.class);
        query.setParameter("group_id", group_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> findAllByGroup(Long group_id, int limit) {
        em.clear();
        TypedQuery<Topic> query = em.createNamedQuery("Topic.findAllByGroup", Topic.class);
        query.setParameter("group_id", group_id);
        if (limit > 0) query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public int countByGroup(Long group_id) {
        TypedQuery<Long> query = em.createNamedQuery("Topic.countByGroup", Long.class);
        query.setParameter("group_id", group_id);
        return query.getResultList().size() > 0 ? query.getSingleResult().intValue() : 0;
    }

    @Override
    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public void delete(Topic topic) {
        topicRepository.delete(topic);
    }
}
