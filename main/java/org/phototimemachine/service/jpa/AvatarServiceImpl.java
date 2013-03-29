package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Avatar;
import org.phototimemachine.repository.AvatarRepository;
import org.phototimemachine.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Service("avatarService")
@Repository
public class AvatarServiceImpl implements AvatarService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AvatarRepository avatarRepository;

    @Override
    public Avatar save(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    @Override
    public Avatar findByUser(Long user_id) {
        em.clear();
        TypedQuery<Avatar> query = em.createNamedQuery("Avatar.findByUser", Avatar.class);
        query.setParameter("user_id", user_id);
        try { return query.getSingleResult(); } catch (Exception ex) { return null; }
    }

    @Override
    public void delete(Avatar avatar) {
        avatarRepository.delete(avatar);
    }
}
