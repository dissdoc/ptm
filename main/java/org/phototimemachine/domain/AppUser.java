package org.phototimemachine.domain;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "AppUser.findById",
                query = "select a from AppUser a where a.userId = :id"),
    @NamedQuery(name = "AppUser.findByFirstAndLastName",
                query = "select distinct a from AppUser a where a.firstName = :firstName and a.lastName = :lastName"),
    @NamedQuery(name = "AppUser.findByEmail",
                query = "select distinct a from AppUser a where a.email = :email"),
    @NamedQuery(name = "AppUser.findByNameAndPassword",
                query = "select distinct a from AppUser a where a.firstName = :firstName and a.lastName = :lastName " +
                        "and a.password = :password"),
    @NamedQuery(name = "AppUser.findAgreeGroup",
                query = "select distinct a from AppUser a left join fetch a.userGroups ug " +
                        "where ug.group.id = :id AND ug.agreed = 1"),
    @NamedQuery(name = "AppUser.findWaitGroup",
                query = "select distinct a from AppUser a left join fetch a.userGroups ug " +
                        "where ug.group.id = :id AND ug.agreed = 0"),
    @NamedQuery(name = "AppUser.findInviteGroup",
                query = "select distinct a from AppUser a left join fetch a.userGroups ug " +
                        "where ug.group.id = :id AND ug.agreed = 2"),
    @NamedQuery(name = "AppUser.findByFBLink",
                query = "select a from AppUser a where a.fblink = :fblink"),
    @NamedQuery(name = "AppUser.findByVKLink",
                query = "select a from AppUser a where a.vklink = :vklink")
})
public class AppUser implements Serializable {

    private Long userId;
    private String firstName;
    private String lastName;
    private String password;
    private String confirmPassword;
    private String email;
    private String about;
    private Boolean gender;
    private Integer day;
    private Integer month;
    private Integer year;
    private String currentCity;
    private String cLongitude;
    private String cLatitude;
    private String homeTown;
    private String hLongitude;
    private String hLatitude;
    private Role role;
    private Avatar avatar;
    private String fblink;
    private String vklink;
    private Set<Photo> photos = new HashSet<Photo>();
    private Set<Fave> faves = new HashSet<Fave>();
    private Set<Comment> comments = new HashSet<Comment>();
    private Set<Album> albums = new HashSet<Album>();
    private Set<Assortment> assortments = new HashSet<Assortment>();
    private Set<Group> groups = new HashSet<Group>();
    private Set<PhotoGroup> photoGroups = new HashSet<PhotoGroup>();
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();
    private Set<Group> joinPhotoGroups = new HashSet<Group>();
    private Set<Photo> joinGroupPhotos = new HashSet<Photo>();
    private Set<Group> joinGroups = new HashSet<Group>();
    private Set<Reply> replies = new HashSet<Reply>();
    private Set<Topic> topics = new HashSet<Topic>();
    private Set<AppUser> contacts = new HashSet<AppUser>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @NotEmpty(message = "{validation.firstname.NotEmpty.message}")
    @Size(min = 3, max = 60, message = "{validation.firstname.Size.message}")
    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotEmpty(message = "{validation.lastname.NotEmpty.message}")
    @Size(min = 3, max = 60, message = "{validation.lastname.Size.message}")
    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotEmpty(message = "{validation.password.NotEmpty.message}")
    @Size(min = 6, max = 256, message = "{validation.password.Size.message}")
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @NotEmpty(message = "{validation.email.NotEmpty.message}")
    @Size(min = 5, max = 256, message = "{validation.password.Size.message}")
    @Email
    @Column(name = "email", unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fblink")
    public String getFblink() {
        return fblink;
    }

    public void setFblink(String fblink) {
        this.fblink = fblink;
    }

    @Column(name = "vklink")
    public String getVklink() {
        return vklink;
    }

    public void setVklink(String vklink) {
        this.vklink = vklink;
    }

    @Column(name = "about")
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Column(name = "gender")
    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String gender() {
        if (gender == null) return "";
        else if (gender == Boolean.FALSE) return "Male";
        else return "Female";
    }

    @Column(name = "day")
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Column(name = "month")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Transient
    public String formatDate() {
        StringBuilder builder = new StringBuilder();

        if (year == null || year <= 0) return null;
        builder.append(year);

        if (month == null || month <= 0) return builder.toString();
        builder.insert(0, months[month] + ", ");

        if (day == null || day <= 0) return builder.toString();
        builder.insert(3, " " + day);
        return builder.toString();
    }

    @Column(name = "current_city")
    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    @Column(name = "c_latitude")
    public String getcLatitude() {
        return cLatitude;
    }

    public void setcLatitude(String cLatitude) {
        this.cLatitude = cLatitude;
    }

    @Column(name = "c_longitude")
    public String getcLongitude() {
        return cLongitude;
    }

    public void setcLongitude(String cLongitude) {
        this.cLongitude = cLongitude;
    }

    @Column(name = "home_town")
    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    @Column(name = "h_latitude")
    public String gethLatitude() {
        return hLatitude;
    }

    public void sethLatitude(String hLatitude) {
        this.hLatitude = hLatitude;
    }

    @Column(name = "h_longitude")
    public String gethLongitude() {
        return hLongitude;
    }

    public void sethLongitude(String hLongitude) {
        this.hLongitude = hLongitude;
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Transient
    public String fullName() {
        return firstName + " " + lastName;
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        photo.setAppUser(this);
        getPhotos().add(photo);
    }

    public void removePhoto(Photo photo) {
        getPhotos().remove(photo);
    }

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Group getGroupById(Long id) {
        for (Group group: getGroups())
            if (group.getId().intValue() == id.intValue()) return group;
        return null;
    }

    public void addGroup(Group group) {
        group.setAuthor(this);
        getGroups().add(group);
    }

    public void removeGroup(Group group) {
        getGroups().remove(group);
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true)
    public Set<Fave> getFaves() {
        return faves;
    }

    public void setFaves(Set<Fave> faves) {
        this.faves = faves;
    }

    public void addFave(Fave fave) {
        fave.setAppUser(this);
        getFaves().add(fave);
    }

    public void removeFave(Fave fave) {
        getFaves().remove(fave);
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true)
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comment.setAppUser(this);
        getComments().add(comment);
    }

    public void removeComment(Comment comment) {
        getComments().remove(comment);
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true)
    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public void addAlbum(Album album) {
        album.setAppUser(this);
        getAlbums().add(album);
    }

    public void removeAlbum(Album album) {
        getAlbums().remove(album);
    }

    public Album getAlbumById(Long id) {
        for (Album album: getAlbums())
            if (album.getId().intValue() == id.intValue()) return album;
        return null;
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true)
    public Set<Assortment> getAssortments() {
        return assortments;
    }

    public void setAssortments(Set<Assortment> assortments) {
        this.assortments = assortments;
    }

    public void addAssortment(Assortment assortment) {
        assortment.setAppUser(this);
        getAssortments().add(assortment);
    }

    public void removeAssortment(Assortment assortment) {
        getAssortments().remove(assortment);
    }

    public Assortment getAssortmentById(Long id) {
        for (Assortment assortment: getAssortments())
            if (assortment.getId().intValue() == id.intValue()) return assortment;
        return null;
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<PhotoGroup> getPhotoGroups() {
        return photoGroups;
    }

    public void setPhotoGroups(Set<PhotoGroup> photoGroups) {
        this.photoGroups = photoGroups;
    }

    public void addPhotoGroup(PhotoGroup photoGroup) {
        photoGroup.setAppUser(this);
        getPhotoGroups().add(photoGroup);
    }

    public void removePhotoGroup(PhotoGroup photoGroup) {
        getPhotoGroups().remove(photoGroup);
    }

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public void addUserGroup(UserGroup userGroup) {
        userGroup.setAppUser(this);
        getUserGroups().add(userGroup);
    }

    public void removeUserGroup(UserGroup userGroup) {
        getUserGroups().remove(userGroup);
    }

    @OneToMany(mappedBy = "author", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply) {
        reply.setAuthor(this);
        getReplies().add(reply);
    }

    public void removeReply(Reply reply) {
        getReplies().remove(reply);
    }

    @OneToMany(mappedBy = "author", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        topic.setAuthor(this);
        getTopics().add(topic);
    }

    public void removeTopic(Topic topic) {
        getTopics().remove(topic);
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "team_id"))
    public Set<Group> getJoinPhotoGroups() {
        return joinPhotoGroups;
    }

    public void setJoinPhotoGroups(Set<Group> joinPhotoGroups) {
        this.joinPhotoGroups = joinPhotoGroups;
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "photo_id"))
    public Set<Photo> getJoinGroupPhotos() {
        return joinGroupPhotos;
    }

    public void setJoinGroupPhotos(Set<Photo> joinGroupPhotos) {
        this.joinGroupPhotos = joinGroupPhotos;
    }

    @ManyToMany
    @JoinTable(name = "user_team",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "team_id"))
    public Set<Group> getJoinGroups() {
        return joinGroups;
    }

    public void setJoinGroups(Set<Group> joinGroups) {
        this.joinGroups = joinGroups;
    }

    @OneToOne(mappedBy = "user")
    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @ManyToMany
    @JoinTable(name = "contact",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id"))
    public Set<AppUser> getContacts() {
        return contacts;
    }

    public void setContacts(Set<AppUser> contacts) {
        this.contacts = contacts;
    }

    public boolean isFriend(Long user_id) {
        for (AppUser user: contacts) {
            if(user.getUserId().intValue() == user_id.intValue())
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        AppUser appUser = (AppUser) otherObject;
        return appUser.getUserId().intValue() == this.getUserId().intValue();
    }

    public String toString() {
        return "User#" + userId + ": " + firstName + " " + lastName + " " + email;
    }

    private static final String[] months = new String[] {
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
}
