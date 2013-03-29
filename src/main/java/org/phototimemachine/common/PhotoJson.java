package org.phototimemachine.common;

public class PhotoJson {

    private Long id;
    private String name;
    private String author;
    private String user;
    private Long userId;
    private String fullDate;
    private String address;
    private String latitude;
    private String longitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullDate() {
        return fullDate;
    }

    public void setFullDate(String startDate, String endDate) {
        this.fullDate = "";

        if (startDate != null && startDate.length() > 1)
            fullDate += startDate;

        if (startDate != null && endDate != null && startDate.length() > 1 && endDate.length() > 1)
            fullDate += " - ";

        if (endDate != null && endDate.length() > 1)
            fullDate += endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
