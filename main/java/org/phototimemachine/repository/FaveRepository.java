package org.phototimemachine.repository;

import org.phototimemachine.domain.Fave;
import org.springframework.data.repository.CrudRepository;

public interface FaveRepository extends CrudRepository<Fave, Long> {
}
