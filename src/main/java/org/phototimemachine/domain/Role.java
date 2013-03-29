package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@NamedQueries({
    @NamedQuery(name = "Role.findById",
                query = "select distinct r from Role r where r.roleId = :roleId")
})
public class Role implements Serializable {

    private String roleId;
    private String description;
    private Set<AppUser> appUsers = new HashSet<AppUser>();

    public Role() {

    }

    public Role(String roleId, String description) {
        this.roleId = roleId;
        this.description = description;
    }

    @Id
    @Column(name = "role_id")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<AppUser> getAppUsers() {
        return appUsers;
    }

    public void setAppUsers(Set<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    public void setAppUser(AppUser appUser) {
        appUser.setRole(this);
        getAppUsers().add(appUser);
    }

    public void removeAppUser(AppUser appUser) {
        getAppUsers().remove(appUser);
    }

    public String toString() {
        return "Role = " + getRoleId() + ": " + description;
    }
}
