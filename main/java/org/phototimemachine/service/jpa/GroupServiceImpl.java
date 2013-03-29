package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Group;
import org.phototimemachine.repository.GroupRepository;
import org.phototimemachine.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("groupService")
@Repository
@Transactional
public class GroupServiceImpl implements GroupService {

    private static Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private GroupRepository groupRepository;

    @Override
    @Transactional(readOnly = true)
    public Group findById(Long id) {
        return groupRepository.findOne(id);
    }

    @Override
    public Group findByIdDetails(Long id) {
        em.clear();
        TypedQuery<Group> query = em.createNamedQuery("Group.findByIdDetails", Group.class);
        query.setParameter("id", id);
        try { return query.getSingleResult(); } catch (Exception ex) { return null; }
    }

    @Override
    public List<Group> findAllByUser(Long user_id) {
        em.clear();
        TypedQuery<Group> query = em.createNamedQuery("Group.findAllByUser", Group.class);
        query.setParameter("id", user_id);
        return query.getResultList();
    }

    @Override
    public List<Group> findFullByUser(Long user_id) {
        em.clear();
        TypedQuery<Group> query = em.createNamedQuery("Group.findFullByUser", Group.class);
        query.setParameter("id", user_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findAllByPhoto(Long photo_id) {
        em.clear();
        TypedQuery<Group> query = em.createNamedQuery("Group.findAllByPhoto", Group.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }
}
