package org.phototimemachine.service.jpa;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.repository.UserRepository;
import org.phototimemachine.service.UserService;
import org.phototimemachine.web.util.user.UserParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import java.util.List;

@Service("userService")
@Repository
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AppUser save(AppUser appUser) {
        return userRepository.save(appUser);
    }

    @Override
    public AppUser findById(Long id) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findById", AppUser.class);
        query.setParameter("id", id);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    @Override
    public AppUser findByFirstAndLastName(String firstName, String lastName) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByFirstAndLastName", AppUser.class);
        query.setParameter("firstName", firstName);
        query.setParameter("lastName", lastName);
        return query.getSingleResult();
    }

    @Override
    public AppUser findByEmail(String email) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByEmail", AppUser.class);
        query.setParameter("email", email);
        return query.getResultList().size() <= 0 ? null : query.getSingleResult();
    }

    @Override
    public boolean isExistEmail(String email) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByEmail", AppUser.class);
        query.setParameter("email", email);
        return query.getResultList().size() > 0;
    }

    @Override
    public boolean isOwner(AppUser appUser) {
        AppUser authUser = findAuthUser();
        if (authUser == null) return false;
        return (appUser.getPassword().equals(authUser.getPassword()) &&
                appUser.getEmail().equals(authUser.getEmail()));
    }

    @Override
    public AppUser findByNameAndPassword(String name, String password) {
        String[] fullName = UserParser.getCorrectUsername(name);
        if (fullName == null) return null;

        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByNameAndPassword", AppUser.class);
        query.setParameter("firstName", fullName[0]);
        query.setParameter("lastName", fullName[1]);
        query.setParameter("password", password);
        return query.getResultList().size() <= 0 ? null : query.getSingleResult();
    }

    @Override
    public AppUser findAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getPrincipal().toString();
        String password = auth.getCredentials().toString();
        return findByNameAndPassword(name, password);
    }

    @Override
    public AppUser findByFBLink(String fblink) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByFBLink", AppUser.class);
        query.setParameter("fblink", fblink);
        try { return query.getSingleResult(); }
        catch (Exception ex) { return null; }
    }

    @Override
    public AppUser findByVKLink(String vklink) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findByVKLink", AppUser.class);
        query.setParameter("vklink", vklink);
        try { return query.getSingleResult(); }
        catch (Exception ex) { return null; }
    }

    @Override
    public List<AppUser> findAgreeGroup(Long group_id) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findAgreeGroup", AppUser.class);
        query.setParameter("id", group_id);
        return query.getResultList();
    }

    @Override
    public List<AppUser> findWaitGroup(Long group_id) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findWaitGroup", AppUser.class);
        query.setParameter("id", group_id);
        return query.getResultList();
    }

    @Override
    public List<AppUser> findInviteGroup(Long group_id) {
        TypedQuery<AppUser> query = em.createNamedQuery("AppUser.findInviteGroup", AppUser.class);
        query.setParameter("id", group_id);
        return query.getResultList();
    }

    @Override
    public boolean isJoinGroup(Long user_id, Long group_id) {
        List<AppUser> users = findAgreeGroup(group_id);
        for (AppUser user: users)
            if (user.getUserId().intValue() == user_id.intValue()) return true;
        return false;
    }

    @Override
    public boolean isWaitGroup(Long user_id, Long group_id) {
        List<AppUser> users = findWaitGroup(group_id);
        for (AppUser user: users)
            if (user.getUserId().intValue() == user_id.intValue()) return true;
        return false;
    }

    @Override
    public boolean isInviteGroup(Long user_id, Long group_id) {
        List<AppUser> users = findInviteGroup(group_id);
        for (AppUser user: users)
            if (user.getUserId().intValue() == user_id.intValue()) return true;
        return false;
    }
}
