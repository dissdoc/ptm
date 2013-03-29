package org.phototimemachine.service;

import org.phototimemachine.domain.Assortment;

import java.util.List;

public interface AssortmentService {

    public Assortment findById(Long id);
    public List<Assortment> findAll();
    public List<Assortment> findAllByUser(Long user_id);
    public List<Assortment> findAllWithDetails();
    public List<Assortment> findAllByPhoto(Long photo_id);
    public Assortment save(Assortment assortment);
    public void delete(Assortment assortment);
}
