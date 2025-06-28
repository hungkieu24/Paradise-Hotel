package Model;

import java.math.BigDecimal;

public class BookingRoomType {
    private int bookingId;
    private int roomTypeId;
    private int quantity;
    private BigDecimal pricePerRoom;
    
    // Additional fields for joined data (optional)
    private String roomTypeName;
    private String roomTypeDescription;
    private String roomTypeImageUrl;
    private String branchName;
    
    // Constructors
    public BookingRoomType() {
    }
    
    public BookingRoomType(int bookingId, int roomTypeId, int quantity, BigDecimal pricePerRoom) {
        this.bookingId = bookingId;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerRoom = pricePerRoom;
    }
    
    // Full constructor with additional fields
    public BookingRoomType(int bookingId, int roomTypeId, int quantity, BigDecimal pricePerRoom,
                          String roomTypeName, String roomTypeDescription, String roomTypeImageUrl) {
        this.bookingId = bookingId;
        this.roomTypeId = roomTypeId;
        this.quantity = quantity;
        this.pricePerRoom = pricePerRoom;
        this.roomTypeName = roomTypeName;
        this.roomTypeDescription = roomTypeDescription;
        this.roomTypeImageUrl = roomTypeImageUrl;
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
    }
    
    public BigDecimal getPricePerRoom() {
        return pricePerRoom;
    }
    
    public void setPricePerRoom(BigDecimal pricePerRoom) {
        this.pricePerRoom = pricePerRoom;
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
    
    // Utility methods
    public BigDecimal getTotalPrice() {
        return pricePerRoom.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public String toString() {
        return "BookingRoomType{" +
                "bookingId=" + bookingId +
                ", roomTypeId=" + roomTypeId +
                ", quantity=" + quantity +
                ", pricePerRoom=" + pricePerRoom +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", totalPrice=" + getTotalPrice() +
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