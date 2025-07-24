package Dal;

import DBcontext.DBContext;
import Model.UserAccount;
import Model.LoyaltyPoint;
import Model.PointTransaction;
import Model.MemberTierHistory;
import Model.BenefitRank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MembershipDAO extends DBContext {

  
    public List<UserAccount> getAllCustomers() throws SQLException {
        List<UserAccount> customers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email, u.phonenumber, u.fullname, u.avatar_url, "
                + "lp.points, lp.level, lp.total_spending "
                + "FROM UserAccount u "
                + "LEFT JOIN LoyaltyPoint lp ON u.id = lp.user_id "
                + "WHERE u.role = 'Customer' AND u.is_deleted = 0 "
                + "ORDER BY u.fullname";

        System.out.println("=== Get All Customers ===");
        System.out.println("SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                UserAccount customer = new UserAccount();
                customer.setId(rs.getString("id"));
                customer.setUsername(rs.getString("username"));
                customer.setEmail(rs.getString("email"));
                customer.setPhonenumber(rs.getString("phonenumber"));
                customer.setFullname(rs.getString("fullname"));
                customer.setAvatar_url(rs.getString("avatar_url"));
                customer.setRole("Customer");

                // Tạo LoyaltyPoint object
                LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
                loyaltyPoint.setUserId(rs.getString("id"));
                loyaltyPoint.setPoints(rs.getInt("points"));
                loyaltyPoint.setLevel(rs.getString("level") != null ? rs.getString("level") : "Member");
                loyaltyPoint.setTotalSpending(rs.getBigDecimal("total_spending") != null ? rs.getBigDecimal("total_spending") : BigDecimal.ZERO);

                customer.setLoyaltyPoint(loyaltyPoint);
                customers.add(customer);

                if (count <= 5) { // Log first 5 customers for debug
                    System.out.println("Customer " + count + ": " + customer.getFullname() + " (" + customer.getId() + ")");
                }
            }
            System.out.println("Total customers loaded: " + count);
        }
        return customers;
    }

    // Tìm kiếm khách hàng theo tên, email hoặc số điện thoại
    public List<UserAccount> searchCustomers(String searchTerm, int branchId) throws SQLException {
        List<UserAccount> customers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email, u.phonenumber, u.fullname, u.avatar_url, "
                + "lp.points, lp.level, lp.total_spending "
                + "FROM UserAccount u "
                + "LEFT JOIN LoyaltyPoint lp ON u.id = lp.user_id "
                + "WHERE u.role = 'Customer' AND u.is_deleted = 0 "
                + "AND (u.fullname LIKE ? OR u.email LIKE ? OR u.phonenumber LIKE ?) "
                + "ORDER BY u.fullname";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserAccount customer = new UserAccount();
                customer.setId(rs.getString("id"));
                customer.setUsername(rs.getString("username"));
                customer.setEmail(rs.getString("email"));
                customer.setPhonenumber(rs.getString("phonenumber"));
                customer.setFullname(rs.getString("fullname"));
                customer.setAvatar_url(rs.getString("avatar_url"));
                customer.setRole("Customer");

                // Tạo LoyaltyPoint object
                LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
                loyaltyPoint.setUserId(rs.getString("id"));
                loyaltyPoint.setPoints(rs.getInt("points"));
                loyaltyPoint.setLevel(rs.getString("level") != null ? rs.getString("level") : "Member");
                loyaltyPoint.setTotalSpending(rs.getBigDecimal("total_spending") != null ? rs.getBigDecimal("total_spending") : BigDecimal.ZERO);

                customer.setLoyaltyPoint(loyaltyPoint);
                customers.add(customer);
            }
        }
        return customers;
    }

    // Lấy thông tin chi tiết khách hàng theo ID
    public UserAccount getCustomerById(String customerId) throws SQLException {
        String sql = "SELECT u.id, u.username, u.email, u.phonenumber, u.fullname, u.avatar_url, u.created_at, "
                + "lp.points, lp.level, lp.total_spending, lp.last_updated "
                + "FROM UserAccount u "
                + "LEFT JOIN LoyaltyPoint lp ON u.id = lp.user_id "
                + "WHERE u.id = ? AND u.role = 'Customer' AND u.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserAccount customer = new UserAccount();
                customer.setId(rs.getString("id"));
                customer.setUsername(rs.getString("username"));
                customer.setEmail(rs.getString("email"));
                customer.setPhonenumber(rs.getString("phonenumber"));
                customer.setFullname(rs.getString("fullname"));
                customer.setAvatar_url(rs.getString("avatar_url"));
                customer.setCreate_at(rs.getString("created_at"));
                customer.setRole("Customer");

                // Tạo LoyaltyPoint object
                LoyaltyPoint loyaltyPoint = new LoyaltyPoint();
                loyaltyPoint.setUserId(rs.getString("id"));
                loyaltyPoint.setPoints(rs.getInt("points"));
                loyaltyPoint.setLevel(rs.getString("level") != null ? rs.getString("level") : "Member");
                loyaltyPoint.setTotalSpending(rs.getBigDecimal("total_spending") != null ? rs.getBigDecimal("total_spending") : BigDecimal.ZERO);
                loyaltyPoint.setLastUpdated(rs.getTimestamp("last_updated"));

                customer.setLoyaltyPoint(loyaltyPoint);
                return customer;
            }
        }
        return null;
    }

    // Lấy lịch sử điểm thưởng
    public List<PointTransaction> getPointHistory(String customerId) throws SQLException {
        List<PointTransaction> history = new ArrayList<>();
        String sql = "SELECT * FROM PointTransaction WHERE user_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PointTransaction transaction = new PointTransaction();
                transaction.setId(rs.getInt("id"));
                transaction.setUserId(rs.getString("user_id"));
                transaction.setPointsChanged(rs.getInt("points_changed"));
                transaction.setChangeType(rs.getString("change_type"));
                transaction.setReason(rs.getString("reason"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));

                history.add(transaction);
            }
        }
        return history;
    }

    // Lấy lịch sử thay đổi hạng thành viên
    public List<MemberTierHistory> getTierHistory(String customerId) throws SQLException {
        List<MemberTierHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM MemberTierHistory WHERE user_id = ? ORDER BY changed_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MemberTierHistory tierHistory = new MemberTierHistory();
                tierHistory.setId(rs.getInt("id"));
                tierHistory.setUserId(rs.getString("user_id"));
                tierHistory.setOldLevel(rs.getString("old_level"));
                tierHistory.setNewLevel(rs.getString("new_level"));
                tierHistory.setReason(rs.getString("reason"));
                tierHistory.setChangedAt(rs.getTimestamp("changed_at"));

                history.add(tierHistory);
            }
        }
        return history;
    }

    // Điều chỉnh điểm thưởng
    public boolean adjustPoints(String customerId, int pointsChange, String reason, String managerId) throws SQLException {
        connection.setAutoCommit(false);

        try {
            // Lấy điểm hiện tại
            String getCurrentPointsSql = "SELECT points FROM LoyaltyPoint WHERE user_id = ?";
            int currentPoints = 0;

            try (PreparedStatement ps = connection.prepareStatement(getCurrentPointsSql)) {
                ps.setString(1, customerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentPoints = rs.getInt("points");
                }
            }

            // Kiểm tra nếu trừ điểm, không được âm
            int newPoints = currentPoints + pointsChange;
            if (newPoints < 0) {
                throw new SQLException("Insufficient points. Current points: " + currentPoints);
            }

            // Cập nhật điểm trong bảng LoyaltyPoint
            String updatePointsSql = "UPDATE LoyaltyPoint SET points = ?, last_updated = GETDATE() WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(updatePointsSql)) {
                ps.setInt(1, newPoints);
                ps.setString(2, customerId);
                ps.executeUpdate();
            }

            // Thêm giao dịch vào lịch sử
            String insertTransactionSql = "INSERT INTO PointTransaction (user_id, points_changed, change_type, reason, created_at) VALUES (?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = connection.prepareStatement(insertTransactionSql)) {
                ps.setString(1, customerId);
                ps.setInt(2, pointsChange);
                ps.setString(3, pointsChange > 0 ? "Earn" : "Change");
                ps.setString(4, reason);
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Thay đổi hạng thành viên
    public boolean changeTier(String customerId, String newLevel, String reason, String managerId) throws SQLException {
        connection.setAutoCommit(false);

        try {
            // Lấy hạng hiện tại
            String getCurrentLevelSql = "SELECT level FROM LoyaltyPoint WHERE user_id = ?";
            String currentLevel = "Member";

            try (PreparedStatement ps = connection.prepareStatement(getCurrentLevelSql)) {
                ps.setString(1, customerId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentLevel = rs.getString("level");
                }
            }

            // Kiểm tra nếu hạng mới giống hạng cũ
            if (currentLevel.equals(newLevel)) {
                throw new SQLException("New tier is the same as current tier");
            }

            // Cập nhật hạng trong bảng LoyaltyPoint
            String updateLevelSql = "UPDATE LoyaltyPoint SET level = ?, last_updated = GETDATE() WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateLevelSql)) {
                ps.setString(1, newLevel);
                ps.setString(2, customerId);
                ps.executeUpdate();
            }

            // Thêm lịch sử thay đổi hạng
            String insertHistorySql = "INSERT INTO MemberTierHistory (user_id, old_level, new_level, reason, changed_at) VALUES (?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = connection.prepareStatement(insertHistorySql)) {
                ps.setString(1, customerId);
                ps.setString(2, currentLevel);
                ps.setString(3, newLevel);
                ps.setString(4, reason);
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Tạo LoyaltyPoint cho khách hàng mới nếu chưa có
    public void createLoyaltyPointIfNotExists(String customerId) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM LoyaltyPoint WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Tạo LoyaltyPoint mới
                String insertSql = "INSERT INTO LoyaltyPoint (user_id, points, level, total_spending, last_updated) VALUES (?, 0, 'Member', 0, GETDATE())";
                try (PreparedStatement insertPs = connection.prepareStatement(insertSql)) {
                    insertPs.setString(1, customerId);
                    insertPs.executeUpdate();
                }
            }
        }
    }

    // Lấy thông tin benefit theo level
    public BenefitRank getBenefitByLevel(String level) throws SQLException {
        String sql = "SELECT * FROM BenefitRank WHERE level = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, level);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BenefitRank benefit = new BenefitRank();
                benefit.setId(rs.getInt("id"));
                benefit.setLevel(rs.getString("level"));
                benefit.setPointRate(rs.getDouble("point_rate"));
                benefit.setDiscountPercent(rs.getDouble("discount_percent"));
                benefit.setBenefit(rs.getString("benefit"));
                benefit.setDeleted(rs.getBoolean("is_deleted"));
                return benefit;
            }
        }
        return null;
    }
}
