package org.phototimemachine.service;

import org.phototimemachine.domain.Album;

import java.util.List;

public interface AlbumService {

    public Album findById(Long id);
    public List<Album> findAll();
    public List<Album> findAllByUser(Long user_id);
    public List<Album> findAllWithDetails();
    public List<Album> findAllByPhoto(Long photo_id);
    public Album save(Album album);
    public void delete(Album album);
}
