package org.phototimemachine.common;

/**
 * Created with IntelliJ IDEA.
 * User: dissdoc
 * Date: 07.03.13
 * Time: 21:20
 * To change this template use File | Settings | File Templates.
 */
public class ReplyJson {

    private String description;
    private String date;
    private String userName;
    private Long id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
