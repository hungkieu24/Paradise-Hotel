package Model;

public class Service {

    private int id;
    private String name;
    private String description;
    private double price;
    private int branchId;
    private String status;
    private String imageUrl;
    private boolean isDeleted;
    // Thêm trường này để lưu trạng thái dịch vụ trong từng booking
    private String bookingServiceStatus;
    private int quantity;

    // Constructors
    public Service() {
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

    // Getter & Setter cho bookingServiceStatus
    public String getBookingServiceStatus() {
        return bookingServiceStatus;
    }

    public void setBookingServiceStatus(String bookingServiceStatus) {
        this.bookingServiceStatus = bookingServiceStatus;
    }
}
