package org.phototimemachine.domain;

import org.phototimemachine.web.util.FormatDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "photo")
@NamedQueries({
    @NamedQuery(name = "Photo.findAll",
                query = "select distinct p from Photo p"),
    @NamedQuery(name = "Photo.findByUser",
                query = "select p from Photo p where p.appUser.userId = :user_id " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findByUserPublic",
                query = "select p from Photo p where p.appUser.userId = :user_id and p.privacy = 0 " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findByAlbum",
                query = "select distinct p from Photo p left join fetch p.injAlbums a where a.id = :album_id " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findByAlbumPublic",
                query = "select distinct p from Photo p left join fetch p.injAlbums a where a.id = :album_id and p.privacy = 0 " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findByFave",
                query = "select distinct p from Photo p left join fetch p.faves f where f.appUser.userId = :user_id " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findByCollection",
                query = "select distinct p from Photo p left join fetch p.assortments a where a.id = :collection_id " +
                        "order by year1, month1, day1, year2, month2, day2"),
    @NamedQuery(name = "Photo.findById",
                query = "select distinct p from Photo p left join fetch p.photoGroups " +
                        "left join fetch p.tags where p.id = :id"),
    @NamedQuery(name = "Photo.findAllWithDetails",
                query = "select distinct p from Photo p left join fetch p.tags " +
                        "left join fetch p.faves left join fetch p.comments"),
    @NamedQuery(name = "Photo.findNotAgreeGroup",
                query = "select distinct p from Photo p left join fetch p.photoGroups pg " +
                        "where pg.group.id = :id AND pg.agreed = 0"),
    @NamedQuery(name = "Photo.findAgreeGroup",
                query = "select distinct p from Photo p left join fetch p.photoGroups pg " +
                        "where pg.group.id = :id AND pg.agreed = 1")
})
public class Photo implements Serializable {

    private Long id;
    private String name;
    private String author;
    private String description;
    private String address;
    private String latitude;
    private String longitude;
    private Boolean marked;
    private Byte license;
    private Byte privacy;
    private Date create_at;
    private AppUser appUser;
    private Integer day1;
    private Integer day2;
    private Integer month1;
    private Integer month2;
    private Integer year1;
    private Integer year2;
    private Integer views;
    private Set<Tag> tags = new HashSet<Tag>();
    private Set<Fave> faves = new HashSet<Fave>();
    private Set<Comment> comments = new HashSet<Comment>();
    private Set<Album> albums = new HashSet<Album>();
    private Set<Album> injAlbums = new HashSet<Album>();
    private Set<Assortment> thumbs = new HashSet<Assortment>();
    private Set<Assortment> assortments = new HashSet<Assortment>();
    private Set<PhotoGroup> photoGroups = new HashSet<PhotoGroup>();
    private Set<Group> groups = new HashSet<Group>();
    private Set<AppUser> joinPhotoGroups = new HashSet<AppUser>();

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

    @Column(name = "author")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Column(name = "marked")
    public  Boolean getMarked() {
        return marked;
    }

    public void setMarked(Boolean marked) {
        this.marked = marked;
    }

    @Column(name = "license")
    public Byte getLicense() {
        return license;
    }

    public void setLicense(Byte license) {
        this.license = license;
    }

    @Transient
    public String nameLicense() {
        if (license > 6) return licenses[0];
        return licenses[license];
    }

    @Column(name = "privacy")
    public Byte getPrivacy() {
        return privacy;
    }

    public void setPrivacy(Byte privacy) {
        this.privacy = privacy;
    }

    @Transient
    public String namePrivacy() {
        switch (privacy) {
            case 0: return "Public";
            case 1: return "Friends";
            default: return "Only me";
        }
    }

    @Column(name = "day1")
    public Integer getDay1() {
        return day1;
    }

    public void setDay1(Integer day1) {
        this.day1 = day1;
    }

    @Column(name = "year2")
    public Integer getYear2() {
        return year2;
    }

    public void setYear2(Integer year2) {
        this.year2 = year2;
    }

    @Column(name = "year1")
    public Integer getYear1() {
        return year1;
    }

    public void setYear1(Integer year1) {
        this.year1 = year1;
    }

    @Column(name = "month2")
    public Integer getMonth2() {
        return month2;
    }

    public void setMonth2(Integer month2) {
        this.month2 = month2;
    }

    @Column(name = "month1")
    public Integer getMonth1() {
        return month1;
    }

    public void setMonth1(Integer month1) {
        this.month1 = month1;
    }

    @Column(name = "day2")
    public Integer getDay2() {
        return day2;
    }

    public void setDay2(Integer day2) {
        this.day2 = day2;
    }

    @Column(name = "views")
    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    @Transient
    public String startDate() {
        StringBuilder builder = new StringBuilder();

        if (year1 <= 0) return null;
        builder.append(year1);

        if (month1 <= 0) return builder.toString();
        builder.insert(0, months[month1] + ", ");

        if (day1 <= 0) return builder.toString();
        builder.insert(0, day1);
        return builder.toString();
    }

    @Transient
    public String endDate() {
        StringBuilder builder = new StringBuilder();

        if (year2 <= 0) return null;
        builder.append(year2);

        if (month2 <= 0) return builder.toString();
        builder.insert(0, months[month2] + ", ");

        if (day2 <= 0) return builder.toString();
        builder.insert(0, day2+" ");
        return builder.toString();
    }

    @Transient
    public String fullDate() {
        String startDate = startDate();
        String endDate = endDate();
        return FormatDate.fulldt(startDate, endDate);
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

    @ManyToMany
    @JoinTable(name = "photo_tag",
               joinColumns = @JoinColumn(name = "photo_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id"))
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Transient
    public boolean isPlace() {
        if (latitude == null && longitude == null) return false;
        if (latitude.trim().length() <= 0 && longitude.trim().length() <= 0) return false;
        return true;
    }

    @OneToMany(mappedBy = "photo", cascade = CascadeType.PERSIST, orphanRemoval = true)
    public Set<Fave> getFaves() {
        return faves;
    }

    public void setFaves(Set<Fave> faves) {
        this.faves = faves;
    }

    public void addFave(Fave fave) {
        fave.setPhoto(this);
        getFaves().add(fave);
    }

    public void removeFave(Fave fave) {
        getFaves().remove(fave);
    }

    @OneToMany(mappedBy = "photo", cascade = CascadeType.PERSIST, orphanRemoval = true)
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comment.setPhoto(this);
        getComments().add(comment);
    }

    public void removeComment(Comment comment) {
        getComments().remove(comment);
    }

    public String toString() {
        return "Photo #" + id + ": " + name;
    }

    @OneToMany(mappedBy = "thumbnail", cascade = CascadeType.MERGE)
    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public void addAlbum(Album album) {
        album.setThumbnail(this);
        getAlbums().add(album);
    }

    public void removeAlbum(Album album) {
        getAlbums().remove(album);
    }

    @ManyToMany
    @JoinTable(name = "photo_album",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "album_id"))
    public Set<Album> getInjAlbums() {
        return injAlbums;
    }

    public void setInjAlbums(Set<Album> injAlbums) {
        this.injAlbums = injAlbums;
    }

    @OneToMany(mappedBy = "thumbnail", cascade = CascadeType.PERSIST)
    public Set<Assortment> getThumbs() {
        return thumbs;
    }

    public void setThumbs(Set<Assortment> thumbs) {
        this.thumbs = thumbs;
    }

    public void addThumb(Assortment thumb) {
        thumb.setThumbnail(this);
        getThumbs().add(thumb);
    }

    public void removeThumb(Assortment thumb) {
        getThumbs().remove(thumb);
    }

    @ManyToMany
    @JoinTable(name = "photo_assortment",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "assortment_id"))
    public Set<Assortment> getAssortments() {
        return assortments;
    }

    public void setAssortments(Set<Assortment> assortments) {
        this.assortments = assortments;
    }

    @OneToMany(mappedBy = "photo", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<PhotoGroup> getPhotoGroups() {
        return photoGroups;
    }

    public void setPhotoGroups(Set<PhotoGroup> photoGroups) {
        this.photoGroups = photoGroups;
    }

    public void addPhotoGroup(PhotoGroup photoGroup) {
        photoGroup.setPhoto(this);
        getPhotoGroups().add(photoGroup);
    }

    public void removePhotoGroup(PhotoGroup photoGroup) {
        getPhotoGroups().remove(photoGroup);
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "photo_id"),
               inverseJoinColumns = @JoinColumn(name = "team_id"))
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @ManyToMany
    @JoinTable(name = "photo_team",
               joinColumns = @JoinColumn(name = "photo_id"),
               inverseJoinColumns = @JoinColumn(name = "user_id"))
    public Set<AppUser> getJoinPhotoGroups() {
        return joinPhotoGroups;
    }

    public void setJoinPhotoGroups(Set<AppUser> joinPhotoGroups) {
        this.joinPhotoGroups = joinPhotoGroups;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        Photo other = (Photo) otherObject;
        return id.intValue() == other.id.intValue();
    }

    @Override
    public int hashCode() {
        return id.toString().hashCode();
    }

    public PhotoGroup getPhotoGroupById(Long group_id) {
        for (PhotoGroup photoGroup: getPhotoGroups())
            if (photoGroup.getGroup().getId().intValue() == group_id.intValue())
                return photoGroup;
        return null;
    }

    private static final String[] months = new String[] {
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static final String[] licenses = new String[] {
            "All Rights Reserved",
            "Attribution-NonCommercial-ShareAlike Creative Commons",
            "Attribution-NonCommercial Creative Commons",
            "Attribution-NonCommercial-NoDerivs Creative Commons",
            "Attribution Creative Commons",
            "Attribution-ShareAlike Creative Commons",
            "Attribution-NoDerivs Creative Commons"};
}
