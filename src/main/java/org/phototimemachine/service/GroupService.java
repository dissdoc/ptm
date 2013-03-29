package org.phototimemachine.service;

import org.phototimemachine.domain.Group;

import java.util.List;

public interface GroupService {

    public Group findById(Long id);
    public Group findByIdDetails(Long id);
    public List<Group> findAllByUser(Long user_id);
    public List<Group> findFullByUser(Long user_id);
    public List<Group> findAllByPhoto(Long photo_id);
    public Group save(Group group);
    public void delete(Group group);
}
