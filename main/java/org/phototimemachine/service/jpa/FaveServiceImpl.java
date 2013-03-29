package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Fave;
import org.phototimemachine.repository.FaveRepository;
import org.phototimemachine.service.FaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("faveService")
@Repository
@Transactional
public class FaveServiceImpl implements FaveService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private FaveRepository faveRepository;

    @Override
    @Transactional(readOnly = true)
    public Fave findByUserAndPhoto(Long user_id, Long photo_id) {
        TypedQuery<Fave> query = em.createNamedQuery("Fave.findByUserAndPhoto", Fave.class);
        query.setParameter("photo_id", photo_id);
        query.setParameter("user_id", user_id);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fave> findByUser(Long user_id) {
        TypedQuery<Fave> query = em.createNamedQuery("Fave.findByUser", Fave.class);
        query.setParameter("user_id", user_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fave> findByPhoto(Long photo_id) {
        TypedQuery<Fave> query = em.createNamedQuery("Fave.findByPhoto", Fave.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    public Fave save(Fave fave) {
        return faveRepository.save(fave);
    }

    @Override
    public void delete(Fave fave) {
        faveRepository.delete(fave);
    }
}
