package Model;

import java.sql.Timestamp;


public class MemberTierHistory {

    private int id;
    private String userId;
    private String oldLevel;
    private String newLevel;
    private Timestamp changedAt;
    private String reason;

    public MemberTierHistory() {
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOldLevel() { return oldLevel; }
    public void setOldLevel(String oldLevel) { this.oldLevel = oldLevel; }

    public String getNewLevel() { return newLevel; }
    public void setNewLevel(String newLevel) { this.newLevel = newLevel; }

    public Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}