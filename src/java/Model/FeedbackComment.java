/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;

/**
 *
 * @author hungk
 */
public class FeedbackComment {
    private int id;
    private int feedback_id;
    private int parent_comment_id;
    private String user_id;
    private String content;
    private String image_url;
    private Timestamp created_at;
    private boolean is_edited;
    private boolean is_deleted;
    
    private Feedback feedback;
    private UserAccount account;

    public FeedbackComment() {
    }

    public FeedbackComment(int id, int feedback_id, int parent_comment_id, String user_id, String content, String image_url, Timestamp created_at, boolean is_edited, boolean is_deleted) {
        this.id = id;
        this.feedback_id = feedback_id;
        this.parent_comment_id = parent_comment_id;
        this.user_id = user_id;
        this.content = content;
        this.image_url = image_url;
        this.created_at = created_at;
        this.is_edited = is_edited;
        this.is_deleted = is_deleted;
    }

    public FeedbackComment(int id, int feedback_id, int parent_comment_id, String user_id, String content, String image_url, Timestamp created_at, boolean is_edited, boolean is_deleted, Feedback feedback) {
        this.id = id;
        this.feedback_id = feedback_id;
        this.parent_comment_id = parent_comment_id;
        this.user_id = user_id;
        this.content = content;
        this.image_url = image_url;
        this.created_at = created_at;
        this.is_edited = is_edited;
        this.is_deleted = is_deleted;
        this.feedback = feedback;
    }

    public FeedbackComment(int id, int feedback_id, int parent_comment_id, String user_id, String content, String image_url, Timestamp created_at, boolean is_edited, boolean is_deleted, Feedback feedback, UserAccount account) {
        this.id = id;
        this.feedback_id = feedback_id;
        this.parent_comment_id = parent_comment_id;
        this.user_id = user_id;
        this.content = content;
        this.image_url = image_url;
        this.created_at = created_at;
        this.is_edited = is_edited;
        this.is_deleted = is_deleted;
        this.feedback = feedback;
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedback_id() {
        return feedback_id;
    }

    public void setFeedback_id(int feedback_id) {
        this.feedback_id = feedback_id;
    }

    public int getParent_comment_id() {
        return parent_comment_id;
    }

    public void setParent_comment_id(int parent_comment_id) {
        this.parent_comment_id = parent_comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public boolean isIs_edited() {
        return is_edited;
    }

    public void setIs_edited(boolean is_edited) {
        this.is_edited = is_edited;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "FeedbackComment{" + "id=" + id + ", feedback_id=" + feedback_id + ", parent_comment_id=" + parent_comment_id + ", user_id=" + user_id + ", content=" + content + ", image_url=" + image_url + ", created_at=" + created_at + ", is_edited=" + is_edited + ", is_deleted=" + is_deleted + '}';
    }
}
