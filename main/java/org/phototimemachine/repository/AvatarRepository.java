package org.phototimemachine.repository;

import org.phototimemachine.domain.Avatar;
import org.springframework.data.repository.CrudRepository;

public interface AvatarRepository extends CrudRepository<Avatar, Long> {
}
