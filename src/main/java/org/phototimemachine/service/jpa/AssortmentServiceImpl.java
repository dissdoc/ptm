package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Assortment;
import org.phototimemachine.repository.AssortmentRepository;
import org.phototimemachine.service.AssortmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("assortmentService")
@Repository
public class AssortmentServiceImpl implements AssortmentService {

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private AssortmentRepository assortmentRepository;

    @Override
    public Assortment findById(Long id) {
        em.clear();
        TypedQuery<Assortment> query = em.createNamedQuery("Assortment.findById", Assortment.class);
        query.setParameter("id", id);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    @Override
    public List<Assortment> findAll() {
        em.clear();
        return em.createNamedQuery("Assortment.findAll", Assortment.class).getResultList();
    }

    @Override
    public List<Assortment> findAllByUser(Long user_id) {
        em.clear();
        TypedQuery<Assortment> query = em.createNamedQuery("Assortment.findAllByUser", Assortment.class);
        query.setParameter("id", user_id);
        return query.getResultList();
    }

    @Override
    public List<Assortment> findAllWithDetails() {
        em.clear();
        return em.createNamedQuery("Assortment.findAllWithDetails", Assortment.class).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assortment> findAllByPhoto(Long photo_id) {
        em.clear();
        TypedQuery<Assortment> query = em.createNamedQuery("Assortment.findAllByPhoto", Assortment.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    public Assortment save(Assortment assortment) {
        return assortmentRepository.save(assortment);
    }

    @Override
    public void delete(Assortment assortment) {
        assortmentRepository.delete(assortment);
    }
}
