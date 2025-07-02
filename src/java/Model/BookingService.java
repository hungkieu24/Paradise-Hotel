package Model;

public class BookingService {
    private int bookingId;
    private int serviceId;
    private int quantity;
    private String paidStatus;
    
    // References
    private String serviceName;
    private java.math.BigDecimal servicePrice;

    public BookingService() {
    }

    public BookingService(int bookingId, int serviceId, int quantity, String paidStatus) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.paidStatus = paidStatus;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
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
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public java.math.BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(java.math.BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }
    
    // Convenience method to calculate total price
    public java.math.BigDecimal getTotalPrice() {
        return servicePrice != null ? servicePrice.multiply(new java.math.BigDecimal(quantity)) : null;
    }
}