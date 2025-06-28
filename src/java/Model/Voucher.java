package Model;

import java.util.Date;

public class Voucher {

    private int id;
    private String code;
    private String description;
    private Integer discountPercent; // phần trăm giảm giá (có thể null)
    private Double discountAmount;   // số tiền giảm giá (có thể null)
    private Double minPrice;         // giá trị tối thiểu để áp dụng
    private int totalQuantity;
    private int usedQuantity;
    private int branchId;
    private Date validFrom;
    private Date validTo;
    private String status;
    private boolean isDeleted;

    // Logic tính giảm giá
    public double calculateDiscount(double totalPrice) {
        if (discountPercent != null && discountPercent > 0) {
            return totalPrice * discountPercent / 100.0;
        } else if (discountAmount != null && discountAmount > 0) {
            return discountAmount;
        }
        return 0;
    }

    public boolean isValid() {
        Date now = new Date();
        return "Active".equalsIgnoreCase(status)
                && (validFrom == null || !now.before(validFrom))
                && (validTo == null || !now.after(validTo))
                && (totalQuantity == 0 || usedQuantity < totalQuantity);
    }

    public Voucher() {
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

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(int usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

}
