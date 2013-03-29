package org.phototimemachine.repository;

import org.phototimemachine.domain.PhotoGroup;
import org.springframework.data.repository.CrudRepository;

public interface PhotoGroupRepository extends CrudRepository<PhotoGroup, Long> {
}
