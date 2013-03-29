package org.phototimemachine.service;

import org.phototimemachine.domain.Fave;

import java.util.List;

public interface FaveService {

    public Fave findByUserAndPhoto(Long user_id, Long photo_id);
    public List<Fave> findByUser(Long user_id);
    public List<Fave> findByPhoto(Long photo_id);
    public Fave save(Fave fave);
    public void delete(Fave fave);
}
