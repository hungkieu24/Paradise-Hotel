/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author KTC
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Voucher {

    private int id;
    private String code;
    private String description;
    private Integer discountPercent;   // Có thể null
    private BigDecimal discountAmount; // Có thể null
    private BigDecimal minPrice;
    private int totalQuantity;
    private int usedQuantity;
    private int branchId;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String status;
    private boolean isDeleted;
    private VoucherRedemptionRule redemptionRule;

    // Constructors
    public Voucher() {
    }

    public Voucher(int id, String code, String description, Integer discountPercent, BigDecimal discountAmount,
            BigDecimal minPrice, int totalQuantity, int usedQuantity, int branchId,
            LocalDateTime validFrom, LocalDateTime validTo, String status, boolean isDeleted) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.minPrice = minPrice;
        this.totalQuantity = totalQuantity;
        this.usedQuantity = usedQuantity;
        this.branchId = branchId;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
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

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
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

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public VoucherRedemptionRule getRedemptionRule() {
        return redemptionRule;
    }

    public void setRedemptionRule(VoucherRedemptionRule redemptionRule) {
        this.redemptionRule = redemptionRule;
    }

}
