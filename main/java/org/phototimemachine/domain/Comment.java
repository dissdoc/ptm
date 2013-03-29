package org.phototimemachine.domain;

import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "comment")
@NamedQueries({
    @NamedQuery(name = "Comment.findById",
                query = "select c from Comment c where c.id = :id"),
    @NamedQuery(name = "Comment.findByPhotoId",
                query = "select distinct c from Comment c left join fetch c.photo p where p.id = :photo_id order by p.id"),
    @NamedQuery(name = "Comment.findByUserId",
                query = "select distinct c from Comment c left join fetch c.appUser a where a.id = :user_id")
})
public class Comment implements Serializable {

    private Long id;
    private String description;
    private AppUser appUser;
    private Photo photo;
    private Date create_at;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    public Date getCreateAt() {
        return create_at;
    }

    public void setCreateAt(Date create_at) {
        this.create_at = create_at;
    }

    @Transient
    public String getDateAgo() {
        PrettyTime p = new PrettyTime();
        return p.format(create_at == null ? new Date() : create_at);
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

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
