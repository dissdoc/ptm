package org.phototimemachine.service;

import org.phototimemachine.domain.Avatar;

public interface AvatarService {

    public Avatar save(Avatar avatar);
    public Avatar findByUser(Long user_id);
    public void delete(Avatar avatar);
}
