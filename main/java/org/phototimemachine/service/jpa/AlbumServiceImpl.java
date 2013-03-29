package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Album;
import org.phototimemachine.repository.AlbumRepository;
import org.phototimemachine.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("albumService")
@Repository
public class AlbumServiceImpl implements AlbumService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public Album findById(Long id) {
        em.clear();
        TypedQuery<Album> query = em.createNamedQuery("Album.findById", Album.class);
        query.setParameter("id", id);
        try { return query.getSingleResult(); } catch (Exception ex) { return null; }
    }

    @Override
    public List<Album> findAll() {
        em.clear();
        return em.createNamedQuery("Album.findAll", Album.class).getResultList();
    }

    @Override
    public List<Album> findAllByUser(Long user_id) {
        em.clear();
        TypedQuery<Album> query = em.createNamedQuery("Album.findAllByUser", Album.class);
        query.setParameter("id", user_id);
        return query.getResultList();
    }

    @Override
    public List<Album> findAllWithDetails() {
        return em.createNamedQuery("Album.findAllWithDetails", Album.class).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Album> findAllByPhoto(Long photo_id) {
        em.clear();
        TypedQuery<Album> query = em.createNamedQuery("Album.findAllByPhoto", Album.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    public Album save(Album album) {
        return albumRepository.save(album);
    }

    @Override
    public void delete(Album album) {
        albumRepository.delete(album);
    }
}
