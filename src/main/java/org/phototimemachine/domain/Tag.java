package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@NamedQueries({
    @NamedQuery(name = "Tag.findAll",
                query = "select t from Tag t"),
    @NamedQuery(name = "Tag.findById",
                query = "select distinct t from Tag t where t.id = :id"),
    @NamedQuery(name = "Tag.findByName",
                query = "select distinct t from Tag t where t.name = :name"),
    @NamedQuery(name = "Tag.findByPhoto",
                query = "select distinct t from Tag t left join fetch t.photos p where p.id = :photo_id")
})
public class Tag implements Serializable {

    private Long id;
    private String name;
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

    @ManyToMany
    @JoinTable(name = "photo_tag",
               joinColumns = @JoinColumn(name = "tag_id"),
               inverseJoinColumns = @JoinColumn(name = "photo_id"))
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "Tag #" + id + " = " + name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
