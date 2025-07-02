
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author thien
 */

public class Service {
    private int id;
    private String name;
    private String description;
    private double price;
    private int branchId;
    private String status;
    private String imageUrl;
    private boolean isDeleted;
    private int quantity;
    private String paid_status;
    // Thêm trường này để lưu trạng thái dịch vụ trong từng booking
    private String bookingServiceStatus;
    private String paidStatus;
    
  
    // Constructors
    public Service() {}

    public Service(int id, String name, String description, double price, int branchId, String status, String imageUrl, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.branchId = branchId;
        this.status = status;
        this.imageUrl = imageUrl;
        this.isDeleted = isDeleted;
    }

    public Service(int id, String name, String description, double price, int branchId, String status, String imageUrl, boolean isDeleted, int quantity, String paid_status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.branchId = branchId;
        this.status = status;
        this.imageUrl = imageUrl;
        this.isDeleted = isDeleted;
        this.quantity = quantity;
        this.paid_status = paid_status;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getBranchId() {
        return branchId;
    }
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean isDeleted() {
        return isDeleted;
    }
  
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
  
      public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }

    // Getter & Setter cho bookingServiceStatus
    public String getBookingServiceStatus() {
        return bookingServiceStatus;
    }

    public void setBookingServiceStatus(String bookingServiceStatus) {
        this.bookingServiceStatus = bookingServiceStatus;
    }
    
    public String getPaid_status() {
        return paid_status;
    }

    public void setPaid_status(String paid_status) {
        this.paid_status = paid_status;
    }
    
    @Override
    public String toString() {
        return "Service{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", price=" + price +
               ", branchId=" + branchId +
               ", status='" + status + '\'' +
               ", imageUrl='" + imageUrl + '\'' +
               ", isDeleted=" + isDeleted +
               '}';
    }
}
