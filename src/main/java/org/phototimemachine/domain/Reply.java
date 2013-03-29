package org.phototimemachine.domain;

import org.ocpsoft.prettytime.PrettyTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reply")
public class Reply implements Serializable {

    private Long id;
    private String description;
    private Date create_at;
    private AppUser author;
    private Topic topic;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    @JoinColumn(name = "topic_id")
    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null) return false;
        if (getClass() != otherObject.getClass()) return false;

        Reply reply = (Reply) otherObject;
        return reply.getId().intValue() == getId().intValue();
    }
}
