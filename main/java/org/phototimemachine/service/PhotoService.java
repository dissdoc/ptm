package org.phototimemachine.service;

import org.phototimemachine.domain.Photo;

import java.util.List;

public interface PhotoService {

    public List<Photo> findAll();
    public List<Photo> findByUser(Long user_id);
    public List<Photo> findByUserPublic(Long user_id);
    public List<Photo> findAllWithDetails();
    public List<Photo> findAgreeGroup(Long group_id);
    public List<Photo> findNotAgreeGroup(Long group_id);
    public List<Photo> findByAlbum(Long album_id);
    public List<Photo> findByAlbumPublic(Long album_id);
    public List<Photo> findByFave(Long user_id);
    public List<Photo> findByCollection(Long collection_id);
    public Photo findById(Long id);
    public Photo save(Photo photo);
    public void delete(Photo photo);
}
