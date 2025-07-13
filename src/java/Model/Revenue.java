/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Timestamp;
import java.sql.Date;

/**
 *
 * @author hungk
 */
public class Revenue {
    private int id;
    private int branch_id;
    private String revenue_type;
    private double amount;
    private Date revenue_date;
    private String source;
    private String description;
    private String created_by;
    private Timestamp created_at;

    public Revenue() {
    }

    public Revenue(int id, int branch_id, String revenue_type, double amount, Date revenue_date, String source, String description, String created_by, Timestamp created_at) {
        this.id = id;
        this.branch_id = branch_id;
        this.revenue_type = revenue_type;
        this.amount = amount;
        this.revenue_date = revenue_date;
        this.source = source;
        this.description = description;
        this.created_by = created_by;
        this.created_at = created_at;
    }

    public Revenue(int branch_id, String revenue_type, double amount, Date revenue_date, String description) {
        this.branch_id = branch_id;
        this.revenue_type = revenue_type;
        this.amount = amount;
        this.revenue_date = revenue_date;
        this.description = description;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getRevenue_type() {
        return revenue_type;
    }

    public void setRevenue_type(String revenue_type) {
        this.revenue_type = revenue_type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getRevenue_date() {
        return revenue_date;
    }

    public void setRevenue_date(Date revenue_date) {
        this.revenue_date = revenue_date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "Revenue{" + "id=" + id + ", branch_id=" + branch_id + ", revenue_type=" + revenue_type + ", amount=" + amount + ", revenue_date=" + revenue_date + ", source=" + source + ", description=" + description + ", created_by=" + created_by + ", created_at=" + created_at + '}';
    }
}
