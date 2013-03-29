package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "album")
@NamedQueries({
    @NamedQuery(name = "Album.findById",
                query = "select a from Album a where a.id = :id"),
    @NamedQuery(name = "Album.findAll",
                query = "select a from Album a"),
    @NamedQuery(name = "Album.findAllByUser",
                query = "select distinct a from Album a left join fetch a.appUser u where u.id = :id"),
    @NamedQuery(name = "Album.findAllByPhoto",
                query = "select distinct a from Album a left join fetch a.photos p where p.id = :photo_id"),
    @NamedQuery(name = "Album.findAllWithDetails",
                query = "select distinct a from Album a left join fetch a.appUser left join fetch a.thumbnail " +
                        "left join fetch a.photos")
})
public class Album implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Date create_at;
    private AppUser appUser;
    private Photo thumbnail;
    private Set<Photo> photos = new HashSet<Photo>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at")
    public Date getCreateAt() {
        return create_at;
    }

    public void setCreateAt(Date create_at) {
        this.create_at = create_at;
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
    @JoinColumn(name = "thumbnail")
    public Photo getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Photo thumbnail) {
        this.thumbnail = thumbnail;
    }

    @ManyToMany
    @JoinTable(name = "photo_album",
               joinColumns = @JoinColumn(name = "album_id"),
               inverseJoinColumns = @JoinColumn(name = "photo_id"))
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public boolean isExistPhoto(Long id) {
        for (Photo photo: getPhotos())
            if (photo.getId().intValue() == id.intValue()) return true;
        return false;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        Album other = (Album) otherObject;
        return id.intValue() == other.id.intValue();
    }

    @Override
    public int hashCode() {
        return id.toString().hashCode();
    }
}
