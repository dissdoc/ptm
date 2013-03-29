package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "photo_team")
@NamedQueries({
    @NamedQuery(name = "PhotoGroup.findByIds",
                query = "select pg from PhotoGroup pg where pg.photo.id = :photo_id AND pg.group.id = :group_id AND " +
                        "pg.appUser.userId = :user_id"),
    @NamedQuery(name = "PhotoGroup.findByPhotoAndUser",
                query = "select pg from PhotoGroup pg where pg.photo.id = :photo_id AND pg.appUser.userId = :user_id"),
    @NamedQuery(name = "PhotoGroup.findByPhotoAndGroup",
                query = "select pg from PhotoGroup pg where pg.photo.id = :photo_id AND pg.group.id = :group_id")
})
public class PhotoGroup implements Serializable {

    private Long id;
    private AppUser appUser;
    private Photo photo;
    private Group group;
    private Boolean agreed;

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
    @JoinColumn(name = "photo_id")
    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
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
    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        PhotoGroup photoGroup = (PhotoGroup) otherObject;
        return photoGroup.id.intValue() == this.id.intValue();
    }

    @Override
    public int hashCode() {
        return id.intValue() + 31;
    }
}
