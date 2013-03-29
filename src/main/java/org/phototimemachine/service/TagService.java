package org.phototimemachine.service;

import org.phototimemachine.domain.Tag;

import java.util.List;

public interface TagService {

    public List<Tag> findAll();
    public List<Tag> findByPhoto(Long photo_id);
    public Tag findById(Long id);
    public Tag findByName(String name);
    public Tag findOrCreate(String name);
    public Tag save(Tag tag);
    public void delete(Tag tag);
}
