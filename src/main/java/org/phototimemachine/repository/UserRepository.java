package org.phototimemachine.repository;

import org.phototimemachine.domain.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<AppUser, Long> {

    public List<AppUser> findByFirstName(String firstName);
}
