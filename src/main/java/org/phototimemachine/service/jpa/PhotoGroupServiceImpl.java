package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.PhotoGroup;
import org.phototimemachine.repository.PhotoGroupRepository;
import org.phototimemachine.service.PhotoGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("photoGroupService")
@Repository
public class PhotoGroupServiceImpl implements PhotoGroupService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PhotoGroupRepository photoGroupRepository;

    @Override
    public PhotoGroup save(PhotoGroup photoGroup) {
        return photoGroupRepository.save(photoGroup);
    }

    @Override
    public PhotoGroup findByIds(Long user_id, Long photo_id, Long group_id) {
        em.clear();
        TypedQuery<PhotoGroup> query = em.createNamedQuery("PhotoGroup.findByIds", PhotoGroup.class);
        query.setParameter("photo_id", photo_id);
        query.setParameter("group_id", group_id);
        query.setParameter("user_id", user_id);
        return query.getSingleResult();
    }

    @Override
    public List<PhotoGroup> findByPhotoAndUser(Long photo_id, Long user_id) {
        em.clear();
        TypedQuery<PhotoGroup> query = em.createNamedQuery("PhotoGroup.findByPhotoAndUser", PhotoGroup.class);
        query.setParameter("photo_id", photo_id);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    public PhotoGroup findByPhotoAndGroup(Long photo_id, Long group_id) {
        em.clear();
        TypedQuery<PhotoGroup> query = em.createNamedQuery("PhotoGroup.findByPhotoAndGroup", PhotoGroup.class);
        query.setParameter("photo_id", photo_id);
        query.setParameter("group_id", group_id);
        try { return query.getSingleResult(); } catch (Exception ex) { return null; }
    }

    @Override
    public void delete(PhotoGroup photoGroup) {
        photoGroupRepository.delete(photoGroup);
    }
}
