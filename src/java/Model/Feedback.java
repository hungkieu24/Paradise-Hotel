/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;

public class Feedback {
    private int id;
    private String user_id;
    private int booking_id;
    private int rating;
    private String comment;
    private String image_url;
    private Timestamp created_at;
    private String status;
    private String admin_action;
    private boolean is_deleted;
    
    private UserAccount userAccount;
    private int roomTypeId;
    // Additional fields for display
    private String username;
    private String userAvatarUrl;

    public Feedback() {
    }
    
    // Constructor with all fields
    public Feedback(int id, String user_id, int booking_id, int rating, 
                   String comment, String image_url, Timestamp created_at, 
                   String status, String admin_action) {
        this.id = id;
        this.user_id = user_id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.image_url = image_url;
        this.created_at = created_at;
        this.status = status;
        this.admin_action = admin_action;
    }

    public Feedback(int rating, String comment, Timestamp created_at, String username, String userAvatarUrl) {
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.username = username;
        this.userAvatarUrl = userAvatarUrl;
    }

    public Feedback(int id, int rating, String comment, Timestamp created_at, int roomTypeId, String username, String userAvatarUrl) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.roomTypeId = roomTypeId;
        this.username = username;
        this.userAvatarUrl = userAvatarUrl;
    }
    
    public Feedback(int id, int booking_id, int rating, String comment, String image_url, Timestamp created_at, String status, String admin_action, UserAccount userAccount) {
        this.id = id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.image_url = image_url;
        this.created_at = created_at;
        this.status = status;
        this.admin_action = admin_action;
        this.userAccount = userAccount;
    }
    public Feedback(String user_id, int booking_id, int rating, String comment, Timestamp created_at, String status) {
        this.user_id = user_id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.status = status;
    }

    public Feedback(String user_id, int booking_id, int rating, String comment, String image_url, Timestamp created_at, String status, String admin_action) {
        this.user_id = user_id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.image_url = image_url;
        this.created_at = created_at;
        this.status = status;
        this.admin_action = admin_action;
    }
    
    public Feedback(int booking_id, int rating, String comment, Timestamp created_at, String username) {
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.username = username;
    }
    
    public Feedback(int rating, String comment, Timestamp created_at, String status) {
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.status = status;
    }

    public Feedback(String user_id, int booking_id, int rating, String comment, Timestamp created_at, String status, String admin_action) {
        this.user_id = user_id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.created_at = created_at;
        this.status = status;
        this.admin_action = admin_action;
    }

    public Feedback(int id, int booking_id, int rating, String comment, String image_url, Timestamp created_at, String status, String admin_action, boolean is_deleted, UserAccount userAccount) {
        this.id = id;
        this.booking_id = booking_id;
        this.rating = rating;
        this.comment = comment;
        this.image_url = image_url;
        this.created_at = created_at;
        this.status = status;
        this.admin_action = admin_action;
        this.is_deleted = is_deleted;
        this.userAccount = userAccount;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    
    
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdmin_action() {
        return admin_action;
    }

    public void setAdmin_action(String admin_action) {
        this.admin_action = admin_action;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    // Utility methods
    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }

    public String getShortComment(int maxLength) {
        if (comment == null) return "";
        if (comment.length() <= maxLength) return comment;
        return comment.substring(0, maxLength) + "...";
    }

    public String getFormattedCreatedAt() {
        if (created_at == null) return "";
        return created_at.toString();
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", user_id='" + user_id + '\'' +
                ", booking_id=" + booking_id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", image_url='" + image_url + '\'' +
                ", created_at=" + created_at +
                ", status='" + status + '\'' +
                ", admin_action='" + admin_action + '\'' +
                ", username='" + username + '\'' +
                ", userAvatarUrl='" + userAvatarUrl + '\'' +
                '}';
    }
}
