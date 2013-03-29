package org.phototimemachine.service;

import org.phototimemachine.domain.PhotoGroup;

import java.util.List;

public interface PhotoGroupService {

    public PhotoGroup save(PhotoGroup photoGroup);
    public PhotoGroup findByIds(Long user_id, Long photo_id, Long group_id);
    public List<PhotoGroup> findByPhotoAndUser(Long photo_id, Long user_id);
    public PhotoGroup findByPhotoAndGroup(Long photo_id, Long group_id);
    public void delete(PhotoGroup photoGroup);
}
