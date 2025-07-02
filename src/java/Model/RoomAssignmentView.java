package Model;

import java.math.BigDecimal;
import java.util.Date;

public class RoomAssignmentView {
    private int bookingId;
    private int roomId;
    private String roomNumber;
    private String roomTypeName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Date checkIn;
    private Date checkOut;
    private Date assignedAt;
    private String bookingStatus;
    private String paymentStatus;
    private BigDecimal totalPrice;
    private String branchName;
    private int branchId;
    private String assignedBy;
    private int nights;
    private String membershipLevel;
    
    // Constructors
    public RoomAssignmentView() {
    }
    
    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }
    
    public int getRoomId() {
        return roomId;
    }
    
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public String getRoomTypeName() {
        return roomTypeName;
    }
    
    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public Date getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }
    
    public Date getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }
    
    public Date getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public String getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public int getBranchId() {
        return branchId;
    }
    
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    
    public String getAssignedBy() {
        return assignedBy;
    }
    
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }
    
    public int getNights() {
        return nights;
    }
    
    public void setNights(int nights) {
        this.nights = nights;
    }
    
    public String getMembershipLevel() {
        return membershipLevel;
    }
    
    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }
    
    // Utility methods
    public String getFormattedTotalPrice() {
        if (totalPrice != null) {
            return String.format("%,.0f VND", totalPrice.doubleValue());
        }
        return "0 VND";
    }
    
    public String getStatusBadgeClass() {
        if (bookingStatus == null) return "bg-secondary";
        
        switch (bookingStatus.toLowerCase()) {
            case "pending": return "bg-warning text-dark";
            case "paid": return "bg-info";
            case "checkedin": return "bg-success";
            case "checkedout": return "bg-primary";
            case "completed": return "bg-success";
            case "cancelled": return "bg-danger";
            case "noshow": return "bg-dark";
            default: return "bg-secondary";
        }
    }
    
    public String getPaymentBadgeClass() {
        if (paymentStatus == null) return "bg-secondary";
        
        switch (paymentStatus.toLowerCase()) {
            case "paid": return "bg-success";
            case "unpaid": return "bg-warning text-dark";
            default: return "bg-secondary";
        }
    }
    
    @Override
    public String toString() {
        return "RoomAssignmentView{" +
                "bookingId=" + bookingId +
                ", roomNumber='" + roomNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", branchName='" + branchName + '\'' +
                '}';
    }
}