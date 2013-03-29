package org.phototimemachine.domain;

import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "topic")
@NamedQueries({
    @NamedQuery(name = "Topic.findById",
                query = "select t from Topic t where t.id = :id"),
    @NamedQuery(name = "Topic.findAllByGroup",
                query = "select distinct t from Topic t where t.group.id = :group_id " +
                        "order by t.id desc"),
    @NamedQuery(name = "Topic.countByGroup",
                query = "select count(distinct t) from Topic t where t.group.id = :group_id")
})
public class Topic implements Serializable {

    private Long id;
    private String theme;
    private String description;
    private Date create_at;
    private AppUser author;
    private Group group;
    private Set<Reply> replies = new HashSet<Reply>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "theme")
    public String getTheme()  {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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

    @Transient
    public String getDateAgo() {
        PrettyTime p = new PrettyTime();
        return p.format(create_at == null ? new Date() : create_at);
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    @ManyToOne
    @JoinColumn(name = "team_id")
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @OneToMany(mappedBy = "topic", cascade = CascadeType.MERGE, orphanRemoval = true, fetch=FetchType.EAGER)
    public Set<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Set<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply) {
        reply.setTopic(this);
        getReplies().add(reply);
    }

    public void removeReply(Reply reply) {
        getReplies().remove(reply);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        Topic topic = (Topic) otherObject;
        return topic.getId().intValue() == getId().intValue();
    }
}
