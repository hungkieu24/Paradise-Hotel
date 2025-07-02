/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import java.sql.Date;
/**
 *
 * @author thien
 */
public class Voucher {
    private int id;
    private String code;
    private String description;
    private double discount_percent;
    private double discount_amount;
    private double min_price;
    private int total_quantity;
    private int used_quantity;
    private int branchId;
    private Date valid_from;
    private Date valid_to;
    private String status;
    private boolean is_deleted;

    public Voucher() {
    }

    public Voucher(int id, String code, String description, double discount_percent, double discount_amount, double min_price, int total_quantity, int used_quantity, int branchId, Date valid_from, Date valid_to, String status, boolean is_deleted) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discount_percent = discount_percent;
        this.discount_amount = discount_amount;
        this.min_price = min_price;
        this.total_quantity = total_quantity;
        this.used_quantity = used_quantity;
        this.branchId = branchId;
        this.valid_from = valid_from;
        this.valid_to = valid_to;
        this.status = status;
        this.is_deleted = is_deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(double discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public double getMin_price() {
        return min_price;
    }

    public void setMin_price(double min_price) {
        this.min_price = min_price;
    }

    public int getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public int getUsed_quantity() {
        return used_quantity;
    }

    public void setUsed_quantity(int used_quantity) {
        this.used_quantity = used_quantity;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Date getValid_from() {
        return valid_from;
    }
  public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_to() {
        return valid_to;
    }

    public void setValid_to(Date valid_to) {
        this.valid_to = valid_to;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
    
    
}