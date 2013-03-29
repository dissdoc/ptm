package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.Tag;
import org.phototimemachine.repository.TagRepository;
import org.phototimemachine.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("tagService")
@Repository
@Transactional
public class TagServiceImpl implements TagService {

    private Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private TagRepository tagRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAll() {
        TypedQuery<Tag> query = em.createNamedQuery("Tag.findAll", Tag.class);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByPhoto(Long photo_id) {
        em.clear();
        TypedQuery<Tag> query = em.createNamedQuery("Tag.findByPhoto", Tag.class);
        query.setParameter("photo_id", photo_id);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findById(Long id) {
        return tagRepository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag findByName(String name) {
        TypedQuery<Tag> query = em.createNamedQuery("Tag.findByName", Tag.class);
        query.setParameter("name", name);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    @Override
    public Tag findOrCreate(String name) {
        Tag tag = findByName(name);
        if (tag != null) return tag;
        tag = new Tag();
        tag.setName(name);
        return save(tag);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        tagRepository.delete(tag);
    }
}
