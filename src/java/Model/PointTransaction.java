package Model;

import java.sql.Timestamp;


public class PointTransaction {

    private int id;
    private String userId;
    private String changeType;
    private int pointsChanged;
    private String reason;
    private Timestamp createdAt;

    public PointTransaction() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public int getPointsChanged() { return pointsChanged; }
    public void setPointsChanged(int pointsChanged) { this.pointsChanged = pointsChanged; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}