package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_team")
@NamedQueries({
    @NamedQuery(name = "UserGroup.findByIds",
                query = "select distinct ug from UserGroup ug where ug.group.id = :group_id AND ug.appUser.userId = :user_id"),
    @NamedQuery(name = "UserGroup.findAllMembers",
                query = "select distinct ug from UserGroup ug where ug.appUser.userId = :user_id AND ug.group.deleted = 0")
})
public class UserGroup implements Serializable {

    private Long id;
    private AppUser appUser;
    private Group group;
    private Byte agreed;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    @ManyToOne
    @JoinColumn(name = "team_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Column(name = "agreed")
    public Byte getAgreed() {
        return agreed;
    }

    public void setAgreed(Byte agreed) {
        this.agreed = agreed;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        UserGroup userGroup = (UserGroup) otherObject;
        return this.id.intValue() == userGroup.id.intValue();
    }
}
