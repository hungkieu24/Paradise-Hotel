package Model;

import java.math.BigDecimal;

public class BookingRoomType {
    private int bookingId;
    private int roomTypeId;
    private int quantity; // Số đêm ở
    private BigDecimal pricePerNight; // Giá mỗi đêm (đổi tên để rõ nghĩa)
    private BigDecimal totalPrice; // Tổng tiền (quantity × pricePerNight)
    
    // Additional fields for joined data (optional)
    private String roomTypeName;
    private String roomTypeDescription;
    private String roomTypeImageUrl;
    private String branchName;
    private double base_price;
    
    // Constructors
    public BookingRoomType() {
    }
    
    public BookingRoomType(int bookingId, int roomTypeId, int quantity, BigDecimal pricePerNight) {
        this.bookingId = bookingId;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerNight = pricePerNight;
        this.totalPrice = calculateTotalPrice();
    }
    
    // Full constructor with additional fields
    public BookingRoomType(int bookingId, int roomTypeId, int quantity, BigDecimal pricePerNight,
                          String roomTypeName, String roomTypeDescription, String roomTypeImageUrl) {
        this.bookingId = bookingId;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerNight = pricePerNight;
        this.roomTypeName = roomTypeName;
        this.roomTypeDescription = roomTypeDescription;
        this.roomTypeImageUrl = roomTypeImageUrl;
        this.totalPrice = calculateTotalPrice();
    }
    
    // Constructor for when totalPrice is already calculated (from database)
    public BookingRoomType(int bookingId, int roomTypeId, int quantity, 
                          BigDecimal pricePerNight, BigDecimal totalPrice) {
        this.bookingId = bookingId;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerNight = pricePerNight;
        this.totalPrice = totalPrice;
    }
    
    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Tự động tính lại tổng tiền khi quantity thay đổi
        if (this.pricePerNight != null) {
            this.totalPrice = calculateTotalPrice();
        }
    }
    
    // Giữ tên cũ để backward compatibility
    public BigDecimal getPricePerRoom() {
        return pricePerNight;
    }
    
    public void setPricePerRoom(BigDecimal pricePerRoom) {
        this.pricePerNight = pricePerRoom;
        // Tự động tính lại tổng tiền khi giá thay đổi
        if (this.quantity > 0) {
            this.totalPrice = calculateTotalPrice();
        }
    }
    
    // Getter/Setter mới với tên rõ nghĩa
    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }
    
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
        if (this.quantity > 0) {
            this.totalPrice = calculateTotalPrice();
        }
    }
    
    public String getRoomTypeName() {
        return roomTypeName;
    }
    
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
    
    public String getRoomTypeDescription() {
        return roomTypeDescription;
    }
    
    public void setRoomTypeDescription(String roomTypeDescription) {
        this.roomTypeDescription = roomTypeDescription;
    }
    
    public String getRoomTypeImageUrl() {
        return roomTypeImageUrl;
    }
    
    public void setRoomTypeImageUrl(String roomTypeImageUrl) {
        this.roomTypeImageUrl = roomTypeImageUrl;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public double getBase_price() {
        return base_price;
    }

    public void setBase_price(double base_price) {
        this.base_price = base_price;
    }
    
    private BigDecimal calculateTotalPrice() {
        if (pricePerNight == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return pricePerNight.multiply(BigDecimal.valueOf(quantity));
    }
    
 
    public BigDecimal getTotalPrice() {
        if (totalPrice != null) {
            return totalPrice;
        }
        return calculateTotalPrice();
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    

    public boolean isDataConsistent() {
        if (totalPrice == null || pricePerNight == null || quantity <= 0) {
            return false;
        }
        BigDecimal calculated = calculateTotalPrice();
        return totalPrice.compareTo(calculated) == 0;
    }
    
    @Override
    public String toString() {
        return "BookingRoomType{" +
                "bookingId=" + bookingId +
                ", roomTypeId=" + roomTypeId +
                ", quantity=" + quantity +
                ", pricePerNight=" + pricePerNight +
                ", totalPrice=" + getTotalPrice() +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", consistent=" + isDataConsistent() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BookingRoomType that = (BookingRoomType) obj;
        return bookingId == that.bookingId && roomTypeId == that.roomTypeId;
    }
    
    @Override
    public int hashCode() {
        return bookingId * 31 + roomTypeId;
    }
}