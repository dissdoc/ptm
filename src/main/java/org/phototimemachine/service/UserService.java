package org.phototimemachine.service;

import org.phototimemachine.domain.AppUser;

import java.util.List;

public interface UserService {

    public AppUser save(AppUser appUser);
    public AppUser findById(Long id);
    public AppUser findByFirstAndLastName(String firstName, String lastName);
    public AppUser findByEmail(String email);
    public AppUser findByNameAndPassword(String name, String password);
    public AppUser findAuthUser();
    public AppUser findByFBLink(String fblink);
    public AppUser findByVKLink(String vklink);
    public List<AppUser> findAgreeGroup(Long group_id);
    public List<AppUser> findWaitGroup(Long group_id);
    public List<AppUser> findInviteGroup(Long group_id);
    public boolean isJoinGroup(Long user_id, Long group_id);
    public boolean isWaitGroup(Long user_id, Long group_id);
    public boolean isInviteGroup(Long user_id, Long group_id);
    public boolean isExistEmail(String email);
    public boolean isOwner(AppUser user);
}
