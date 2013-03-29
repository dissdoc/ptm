package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Photo;
import org.phototimemachine.repository.PhotoRepository;
import org.phototimemachine.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("photoService")
@Repository
@Transactional
public class PhotoServiceImpl implements PhotoService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Photo> findAll() {
        em.clear();
        List<Photo> photos = em.createNamedQuery("Photo.findAll", Photo.class).getResultList();
        return photos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Photo> findByUser(Long user_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByUser", Photo.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Photo> findByUserPublic(Long user_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByUserPublic", Photo.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Photo> findAllWithDetails() {
        em.clear();
        List<Photo> photos = em.createNamedQuery("Photo.findAllWithDetails", Photo.class).getResultList();
        return photos;
    }

    @Override
    public List<Photo> findAgreeGroup(Long group_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findAgreeGroup", Photo.class);
        query.setParameter("id", group_id);
        return query.getResultList();
    }

    @Override
    public List<Photo> findNotAgreeGroup(Long group_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findNotAgreeGroup", Photo.class);
        query.setParameter("id", group_id);
        return query.getResultList();
    }

    @Override
    public List<Photo> findByAlbum(Long album_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByAlbum", Photo.class);
        query.setParameter("album_id", album_id);
        return query.getResultList();
    }

    @Override
    public List<Photo> findByAlbumPublic(Long album_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByAlbumPublic", Photo.class);
        query.setParameter("album_id", album_id);
        return query.getResultList();
    }

    @Override
    public List<Photo> findByFave(Long user_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByFave", Photo.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    public List<Photo> findByCollection(Long collection_id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findByCollection", Photo.class);
        query.setParameter("collection_id", collection_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Photo findById(Long id) {
        em.clear();
        TypedQuery<Photo> query = em.createNamedQuery("Photo.findById", Photo.class);
        query.setParameter("id", id);
        try { return query.getSingleResult(); }
        catch(Exception ex) { return null; }
    }

    @Override
    public Photo save(Photo photo) {
        em.clear();
        if (findById(photo.getId()) != null)
            em.merge(photo);
        else
            em.persist(photo);
        return photo;
//        return photoRepository.save(photo);
    }

    @Override
    public void delete(Photo photo) {
        photoRepository.delete(photo);
    }
}
