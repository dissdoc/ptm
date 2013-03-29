package org.phototimemachine.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "avatar")
@NamedQueries({
    @NamedQuery(name = "Avatar.findByUser",
                query = "select a from Avatar a where a.user.userId = :user_id")
})
public class Avatar implements Serializable {

    private Long id;
    private AppUser user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
