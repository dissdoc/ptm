package org.phototimemachine.service;

import org.phototimemachine.domain.UserGroup;

import java.util.List;

public interface UserGroupService {

    public UserGroup save(UserGroup userGroup);
    public UserGroup findByIds(Long group_id, Long user_id);
    public List<UserGroup> findAllMembers(Long user_id);
    public void delete(UserGroup userGroup);
}
