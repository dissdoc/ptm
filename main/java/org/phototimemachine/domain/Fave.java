package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "fave")
@NamedQueries({
    @NamedQuery(name = "Fave.findByUserAndPhoto",
                query = "select f from Fave f  where f.appUser.id = :user_id AND f.photo.id = :photo_id"),
    @NamedQuery(name = "Fave.findByUser",
                query = "select distinct f from Fave f left join fetch f.appUser a left join fetch f.photo p " +
                        "where a.id = :user_id"),
    @NamedQuery(name = "Fave.findByPhoto",
                query = "select distinct f from Fave f left join fetch f.photo p left join fetch f.appUser a " +
                        "where p.id = :photo_id")
})
public class Fave implements Serializable {

    private Long id;
    private AppUser appUser;
    private Photo photo;

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
}
