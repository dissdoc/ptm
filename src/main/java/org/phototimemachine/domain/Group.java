package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "team")
@NamedQueries({
    @NamedQuery(name = "Group.findByIdDetails",
                query = "select distinct g from Group g where g.id = :id"),
    @NamedQuery(name = "Group.findAllByUser",
                query = "select g from Group g where g.author.userId = :id"),
    @NamedQuery(name = "Group.findFullByUser",
                query = "select distinct g from Group g left join fetch g.userGroups ug " +
                        "where g.author.userId = :id OR ug.appUser.userId = :id"),
    @NamedQuery(name = "Group.findAllByPhoto",
                query = "select distinct g from Group g left join fetch g.photoGroups pg " +
                        "where pg.photo.id = :photo_id and pg.agreed = 1")
})
public class Group implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String rules;
    private Boolean closed;
    private Boolean marked;
    private Boolean deleted;
    private Date create_at;
    private AppUser author;
    private Set<PhotoGroup> photoGroups = new HashSet<PhotoGroup>();
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();
    private Set<Photo> photos = new HashSet<Photo>();
    private Set<AppUser> joinPhotoUsers = new HashSet<AppUser>();
    private Set<Topic> topics = new HashSet<Topic>();

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

    @Column(name = "rules")
    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    @Column(name = "marked")
    public Boolean getMarked() {
        return marked;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }

    @Column(name = "deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "closed")
    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
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
    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    @OneToMany(mappedBy = "group", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<PhotoGroup> getPhotoGroups() {
        return photoGroups;
    }

    public void setPhotoGroups(Set<PhotoGroup> photoGroups) {
        this.photoGroups = photoGroups;
    }

    public void addPhotoGroup(PhotoGroup photoGroup) {
        photoGroup.setGroup(this);
        getPhotoGroups().add(photoGroup);
    }

    public void removePhotoGroup(PhotoGroup photoGroup) {
        getPhotoGroups().remove(photoGroup);
    }

    @OneToMany(mappedBy = "group", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public void addUserGroup(UserGroup userGroup) {
        userGroup.setGroup(this);
        getUserGroups().add(userGroup);
    }

    public void removeUserGroup(UserGroup userGroup) {
        getUserGroups().remove(userGroup);
    }

    public boolean isExistPhoto(Long id) {
        for (Photo photo: joinPhotos())
            if (photo.getId().intValue() == id.intValue()) return true;
        return false;
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "team_id"),
               inverseJoinColumns = @JoinColumn(name = "photo_id"))
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "team_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<AppUser> getJoinPhotoUsers() {
        return joinPhotoUsers;
    }

    public void setJoinPhotoUsers(Set<AppUser> joinPhotoUsers) {
        this.joinPhotoUsers = joinPhotoUsers;
    }

    public Set<Photo> joinPhotos() {
        Set<Photo> joinPhotos = new HashSet<Photo>();
        for (PhotoGroup photoGroup: getPhotoGroups())
            joinPhotos.add(photoGroup.getPhoto());
        return joinPhotos;
    }

    @OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST, orphanRemoval = true)
    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        topic.setGroup(this);
        getTopics().add(topic);
    }

    public void removeTopic(Topic topic) {
        getTopics().remove(topic);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        Group other = (Group) otherObject;
        return id.intValue() == other.id.intValue();
    }
}
