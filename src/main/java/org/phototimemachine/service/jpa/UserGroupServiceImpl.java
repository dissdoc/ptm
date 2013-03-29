package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.UserGroup;
import org.phototimemachine.repository.UserGroupRepository;
import org.phototimemachine.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("userGroupService")
@Repository
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Override
    public UserGroup save(UserGroup userGroup) {
        return userGroupRepository.save(userGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGroup findByIds(Long group_id, Long user_id) {
        em.clear();
        TypedQuery<UserGroup> query = em.createNamedQuery("UserGroup.findByIds", UserGroup.class);
        query.setParameter("group_id", group_id);
        query.setParameter("user_id", user_id);
        try { return query.getSingleResult(); } catch (Exception ex) { return null; }
    }

    @Override
    public List<UserGroup> findAllMembers(Long user_id) {
        em.clear();
        TypedQuery<UserGroup> query = em.createNamedQuery("UserGroup.findAllMembers", UserGroup.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    public void delete(UserGroup userGroup) {
        userGroupRepository.delete(userGroup);
    }
}
