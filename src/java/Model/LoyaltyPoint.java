/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author KTC
 */
public class LoyaltyPoint {

    private String user_id;
    private int points;
    private String level;
    private Date last_updated;
    private Date expired_at;
    private int discountPercent;

    public LoyaltyPoint() {
    }

    public LoyaltyPoint(String user_id, int points, String level, Date last_updated, Date expired_at) {
        this.user_id = user_id;
        this.points = points;
        this.level = level;
        this.last_updated = last_updated;
        this.expired_at = expired_at;
    }

    public LoyaltyPoint(String user_id, int points, String level, Date last_updated, Date expired_at, int discountPercent) {
        this.user_id = user_id;
        this.points = points;
        this.level = level;
        this.last_updated = last_updated;
        this.expired_at = expired_at;
        this.discountPercent = discountPercent;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }
    
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public Date getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(Date expired_at) {
        this.expired_at = expired_at;
    }

}
