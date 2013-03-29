package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Role;
import org.phototimemachine.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Service("roleService")
@Repository
@Transactional
public class RoleServiceImpl implements RoleService {

    private Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public Role findById(String roleId) {
        TypedQuery<Role> query = em.createNamedQuery("Role.findById", Role.class);
        query.setParameter("roleId", roleId);
        return query.getSingleResult();
    }
}
