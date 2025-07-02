/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import DBcontext.DBContext;
import java.sql.*;
import Model.UserAccount;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author thien
 */
public class UserAccountDAO extends DBContext {

    public UserAccount login(String username, String password) {
        String sql = "SELECT * FROM UserAccount WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }
                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password, String email, String avatar_url, String phonenumber) {
        try {
            // 1. Tìm ID lớn nhất hiện có
            String getMaxIdSql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) AS maxId FROM UserAccount";
            PreparedStatement ps1 = connection.prepareStatement(getMaxIdSql);
            ResultSet rs = ps1.executeQuery();

            String newId = "U001";
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                newId = String.format("U%03d", maxId + 1);
            }

            // 2. Thêm người dùng mới
            String insertUserSql = "INSERT INTO UserAccount (id, username, password, email, avatar_url, role, status, phonenumber) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(insertUserSql);
            ps2.setString(1, newId);
            ps2.setString(2, username);
            ps2.setString(3, password);
            ps2.setString(4, email);
            ps2.setString(5, avatar_url);
            ps2.setString(6, "Customer");
            ps2.setString(7, "Active");
            ps2.setString(8, phonenumber);

            int rowsUser = ps2.executeUpdate();

            // 3. Nếu thêm user thành công thì thêm LoyaltyPoint
            if (rowsUser > 0) {
                String insertLoyaltySql = "INSERT INTO LoyaltyPoint (user_id, points, level) VALUES (?, ?, ?)";
                PreparedStatement ps3 = connection.prepareStatement(insertLoyaltySql);
                ps3.setString(1, newId);
                ps3.setInt(2, 0);  // default points
                ps3.setString(3, "Member");  // default level
                int rowsLoyalty = ps3.executeUpdate();

                // 4. Ghi vào MemberTierHistory nếu thêm điểm thành công
                if (rowsLoyalty > 0) {
                    String insertHistorySql = "INSERT INTO MemberTierHistory (user_id, old_level, new_level, reason) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps4 = connection.prepareStatement(insertHistorySql);
                    ps4.setString(1, newId);
                    ps4.setString(2, "Member");  // không có cấp trước đó
                    ps4.setString(3, "Member");  // cấp mới
                    ps4.setString(4, "Registered new account");

                    int rowsHistory = ps4.executeUpdate();
                    return rowsHistory > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExist(String email) {
        String sql = "SELECT 1 FROM UserAccount WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUsernameExist(String username) {
        String sql = "SELECT 1 FROM UserAccount WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserAccount getUserByEmail(String email) {
        String sql = "SELECT * FROM UserAccount WHERE email = ? AND status = 'Active'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserAccount saveUserToDatabase(String email, String name, String avatar_url) {
        try {
            String checkSql = "SELECT * FROM UserAccount WHERE email= ?";
            PreparedStatement ps = connection.prepareStatement(checkSql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;// doc tu database
                String updateSql = "update UserAccount set username = ?, avatar_url = ?, role = ?, status=?, created_at=? where email =?";
                PreparedStatement pss = connection.prepareStatement(updateSql);
                pss.setString(1, name);
                pss.setString(2, avatar_url);
                pss.setString(3, rs.getString("role"));
                pss.setString(4, rs.getString("status"));
                pss.setString(5, createdAt);
                pss.setString(6, email);
                pss.executeUpdate();

                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }

                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
            } else {
                // Thêm người dùng mới
                String getMaxIdSql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) AS maxId FROM UserAccount";
                PreparedStatement ps1 = connection.prepareStatement(getMaxIdSql);
                rs = ps1.executeQuery();

                String newId = "U001";
                if (rs.next()) {
                    int maxId = rs.getInt("maxId");
                    newId = String.format("U%03d", maxId + 1);
                }
                String insertSql = "Insert into UserAccount (id, username, password, email, avatar_url, role, status, created_at)"
                        + "values(?, ?, ?, ?, ?,?,?,?)";
                ps = connection.prepareStatement(insertSql);
                ps.setString(1, newId);
                ps.setString(2, name);
                ps.setString(3, "123");// đặt mặc định là 123
                ps.setString(4, email);
                ps.setString(5, avatar_url);
                ps.setString(6, "Customer");// đặt mặc định là customer
                ps.setString(7, "Active"); //dat mac dinh la active
                ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
                // Vì vừa insert nên branchId sẽ là null cho user mới (customer)
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;// doc tu database
                return new UserAccount(newId, name, "123", email, avatar_url, "Customer", "Active", createdAt, null, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserAccount getUserById(String id) {
        String sql = "SELECT * FROM UserAccount WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserInfo(String userId, String username, String email, String phoneNumber, String avatarUrl) {
        String sql = "UPDATE UserAccount SET username = ?, email = ?, phonenumber = ?, avatar_url = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);
            ps.setString(4, avatarUrl);
            ps.setString(5, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEmail(String userId, String email) {
        String sql = "UPDATE UserAccount SET email = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public UserAccount getHotelOwner() {
        String sql = "SELECT TOP 1 * FROM UserAccount WHERE role = 'HotelOwner' AND status = 'Active'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy hotel owner hoặc lỗi xảy ra
    }

    public List<UserAccount> getAllStaff() {
        List<UserAccount> staffList = new ArrayList<>();
        String sql = "SELECT * FROM UserAccount WHERE role = 'Staff'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Integer branchId = null;
                try {
                    branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
                } catch (Exception e) {
                    branchId = null;
                }
                Timestamp ts = rs.getTimestamp("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                UserAccount staff = new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        branchId
                );
                staffList.add(staff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffList;
    }

    public boolean updateUserRoleToManager(String userId) {
        String sql = "UPDATE UserAccount SET role = 'Manager' WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePassword(String email, String password) {
        String sql = "update UserAccount set password = ? where email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, password);
            ps.setString(2, email);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFieldExists(String fieldName, String value, String excludeId) {
        String sql = "SELECT 1 FROM UserAccount WHERE " + fieldName + " = ?" + (excludeId != null ? " AND id != ?" : "");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            if (excludeId != null) {
                ps.setString(2, excludeId);
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserAccount getUserByUserName(String username) {
        String sql = "select * from UserAccount where username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("created_at"),
                        rs.getString("phonenumber")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //lay thong tin khach hang
    public UserAccount getUserInfoById(String userId) {
        String sql = "SELECT u.*, lp.level AS rank " +
                     "FROM UserAccount u " +
                     "LEFT JOIN LoyaltyPoint lp ON u.id = lp.user_id " +
                     "WHERE u.id = ?";
        try (
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserAccount user = new UserAccount();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setFullname(rs.getString("fullname"));
                user.setEmail(rs.getString("email"));
                user.setPhonenumber(rs.getString("phonenumber"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setAvatar_url(rs.getString("avatar_url"));
                user.setRank(rs.getString("rank")); // lấy từ LoyaltyPoint
                // Nếu UserAccount có các trường khác, map thêm tại đây
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy staff theo ID (nếu cần xác thực staff)
    public UserAccount getStaffById(String id) {
        String sql = "SELECT * FROM UserAccount WHERE id = ? AND role = 'Staff'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserAccount(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm tiện ích mapping ResultSet về UserAccount
    private UserAccount extractUserAccount(ResultSet rs) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = null;
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            createdAtStr = sdf.format(ts);
        }
        Integer branchId = rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null;
        return new UserAccount(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("avatar_url"),
                rs.getString("role"),
                rs.getString("status"),
                createdAtStr,
                rs.getString("phonenumber"),
                branchId
        );
    }

    // Lấy User bằng phone (ưu tiên số điện thoại)
    public UserAccount getUserByPhone(String phone) {
        String sql = "SELECT * FROM UserAccount WHERE phonenumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserAccount(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserAccount getUserByEmailOrPhone(String keyword) {
        String sql = "SELECT * FROM UserAccount WHERE email = ? OR phonenumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserAccount(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm user theo id
    public UserAccount findById(String id) {
        String sql = "SELECT * FROM UserAccount WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // hoang create
    // Loại bỏ hoặc không sử dụng checkPassword nếu servlet đã kiểm tra
    public boolean checkPassword(String username, String password) {
        String sql = "SELECT password FROM UserAccount WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, storedHashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Cập nhật updatePassword1 để chấp nhận mật khẩu đã mã hóa
    public boolean updatePassword1(String username, String newPassword) {
        String sql = "UPDATE UserAccount SET password = ? WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newPassword); // newPassword đã được mã hóa trong servlet
            ps.setString(2, username);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
  
    // author : hung
    // Content: get all user account
    public List<UserAccount> getAllUsersAccount() {
        List<UserAccount> userList = new ArrayList<>();
        String sql = "SELECT ua.*, hb.name AS branch_name "
                + "FROM UserAccount ua "
                + "LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                String createdAt = (ts != null) ? sdf.format(ts) : null;
                Timestamp ts2 = rs.getTimestamp("last_login_at");
                String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                UserAccount user = new UserAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("avatar_url"),
                        rs.getString("role"),
                        rs.getString("status"),
                        createdAt,
                        rs.getString("phonenumber"),
                        rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                        rs.getString("branch_name"),
                        rs.getString("fullname"),
                        rs.getString("login_type"),
                        rs.getBoolean("is_deleted"),
                        lastLogin
                );

                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // author : hung
    // Content: get all user account's role
    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT role FROM UserAccount WHERE is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }

    // author : hung
    // Content: get all user account's status
    public List<String> getAllStatuses() {
        List<String> statuses = new ArrayList<>();
        String sql = "SELECT DISTINCT status FROM UserAccount WHERE is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                statuses.add(rs.getString("status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statuses;
    }

    // author : hung
    // Content: get all user account by page
    public List<UserAccount> getAllUsersAccountByPage(int page, int pageSize) {
        List<UserAccount> userList = new ArrayList<>();

        String sql = "SELECT ua.*, hb.name AS branch_name "
                + "FROM UserAccount ua "
                + "LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id "
                + "ORDER BY ua.id "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    String createdAt = (ts != null) ? sdf.format(ts) : null;
                    Timestamp ts2 = rs.getTimestamp("last_login_at");
                    String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                    UserAccount user = new UserAccount(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("avatar_url"),
                            rs.getString("role"),
                            rs.getString("status"),
                            createdAt,
                            rs.getString("phonenumber"),
                            rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                            rs.getString("branch_name"),
                            rs.getString("fullname"),
                            rs.getString("login_type"),
                            rs.getBoolean("is_deleted"),
                            lastLogin
                    );

                    userList.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // author : hung
    // Content: search user account 
    public List<UserAccount> searchUserAccounts(String keyword, int page, int pageSize) {
        List<UserAccount> userList = new ArrayList<>();

        String sql = "SELECT ua.*, hb.name AS branch_name "
                + "FROM UserAccount ua "
                + "LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id "
                + "WHERE ua.username LIKE ? OR ua.fullname LIKE ? OR ua.email LIKE ? OR ua.phonenumber LIKE ? "
                + "OR ua.role LIKE ? OR ua.status LIKE ? OR ua.login_type LIKE ? "
                + "ORDER BY ua.id "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String wildcardKeyword = "%" + keyword + "%";
            for (int i = 1; i <= 7; i++) {
                stmt.setString(i, wildcardKeyword);
            }
            stmt.setInt(8, (page - 1) * pageSize);
            stmt.setInt(9, pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    String createdAt = (ts != null) ? sdf.format(ts) : null;
                    Timestamp ts2 = rs.getTimestamp("last_login_at");
                    String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                    UserAccount user = new UserAccount(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("avatar_url"),
                            rs.getString("role"),
                            rs.getString("status"),
                            createdAt,
                            rs.getString("phonenumber"),
                            rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                            rs.getString("branch_name"),
                            rs.getString("fullname"),
                            rs.getString("login_type"),
                            rs.getBoolean("is_deleted"),
                            lastLogin
                    );

                    userList.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // author : hung
    // Content: count account after search
    public int getTotalUserAccountAfterSearching(String keyword) {
        String sql = "SELECT COUNT(*) FROM UserAccount "
                + "WHERE username LIKE ? OR fullname LIKE ? OR email LIKE ? OR phonenumber LIKE ? OR role LIKE ? OR status LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String wildcardKeyword = "%" + keyword + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, wildcardKeyword);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // author : hung
    // Content: count account after select filer by status
    public int getTotalUserAccountByStatus(String status) {
        // Nếu là Active thì cần thêm điều kiện is_deleted = 0
        boolean filterDeleted = "Active".equalsIgnoreCase(status);

        String sql = "SELECT COUNT(*) FROM UserAccount WHERE status = ? "
                + (filterDeleted ? "AND is_deleted = 0" : "");

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // author : hung
    // Content: get account after select filer by status
    public List<UserAccount> getUserAccountsByStatus(String status, int page, int pageSize) {
        List<UserAccount> userList = new ArrayList<>();

        String sql = """
        SELECT ua.*, hb.name AS branch_name
        FROM UserAccount ua
        LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id
        WHERE ua.status = ?
        %s
        ORDER BY ua.id
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        // Thêm điều kiện is_deleted nếu status là Active
        boolean filterDeleted = "Active".equalsIgnoreCase(status);
        String whereDeletedClause = filterDeleted ? "AND ua.is_deleted = 0" : "";

        sql = String.format(sql, whereDeletedClause);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    String createdAt = (ts != null) ? sdf.format(ts) : null;
                    Timestamp ts2 = rs.getTimestamp("last_login_at");
                    String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                    UserAccount user = new UserAccount(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("avatar_url"),
                            rs.getString("role"),
                            rs.getString("status"),
                            createdAt,
                            rs.getString("phonenumber"),
                            rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                            rs.getString("branch_name"),
                            rs.getString("fullname"),
                            rs.getString("login_type"),
                            rs.getBoolean("is_deleted"),
                            lastLogin
                    );

                    userList.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // author : hung
    // Content: get account after select status is deleted
    public int getTotalDeletedUserAccounts() {
        String sql = "SELECT COUNT(*) FROM UserAccount WHERE is_deleted = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // author : hung
    // Content: get total account after select status is deleted
    public List<UserAccount> getDeletedUserAccounts(int page, int pageSize) {
        List<UserAccount> userList = new ArrayList<>();

        String sql = """
        SELECT ua.*, hb.name AS branch_name
        FROM UserAccount ua
        LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id
        WHERE ua.is_deleted = 1
        ORDER BY ua.id
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    String createdAt = (ts != null) ? sdf.format(ts) : null;
                    Timestamp ts2 = rs.getTimestamp("last_login_at");
                    String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                    UserAccount user = new UserAccount(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("avatar_url"),
                            rs.getString("role"),
                            rs.getString("status"),
                            createdAt,
                            rs.getString("phonenumber"),
                            rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                            rs.getString("branch_name"),
                            rs.getString("fullname"),
                            rs.getString("login_type"),
                            rs.getBoolean("is_deleted"),
                            lastLogin
                    );

                    userList.add(user);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // author: hung
    // Content: Cập nhật giá trị bất kỳ cột nào trong bảng UserAccount
    public boolean updateUserField(String fieldName, Object newValue, String userId) {
        String sql = "UPDATE UserAccount SET " + fieldName + " = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (newValue instanceof String) {
                ps.setString(1, (String) newValue);
            } else if (newValue instanceof Integer) {
                ps.setInt(1, (Integer) newValue);
            } else if (newValue instanceof Boolean) {
                ps.setBoolean(1, (Boolean) newValue);
            } else if (newValue instanceof Timestamp) {
                ps.setTimestamp(1, (Timestamp) newValue);
            } else {
                ps.setObject(1, newValue);
            }

            ps.setString(2, userId);

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // author : hung
    // Content: get useraccount by id follow new constructor
    public UserAccount getUserAccountById(String userId) {
        String sql = "SELECT ua.*, hb.name AS branch_name "
                + "FROM UserAccount ua "
                + "LEFT JOIN HotelBranch hb ON ua.branch_id = hb.id "
                + "WHERE ua.id = ? ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
           try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Timestamp ts = rs.getTimestamp("created_at");
                    String createdAt = (ts != null) ? sdf.format(ts) : null;
                    Timestamp ts2 = rs.getTimestamp("last_login_at");
                    String lastLogin = (ts2 != null) ? sdf.format(ts2) : null;

                    return new UserAccount(
                            rs.getString("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("avatar_url"),
                            rs.getString("role"),
                            rs.getString("status"),
                            createdAt,
                            rs.getString("phonenumber"),
                            rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null,
                            rs.getString("branch_name"),
                            rs.getString("fullname"),
                            rs.getString("login_type"),
                            rs.getBoolean("is_deleted"),
                            lastLogin
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Không tìm thấy user
    }

     // Lấy thông tin user bởi id (int)
    public UserAccount findAccountByNumericId(int numericId) {
        String sql = "SELECT * FROM UserAccount WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, numericId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy thông tin user bởi id (String)
    public UserAccount lookupAccountById(String userId) {
        String sql = "SELECT * FROM UserAccount WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy thông tin user đầy đủ (dùng cho hiển thị khách hàng/checkout) (int)
    public UserAccount fetchFullAccountByNumericId(int numericId) {
        return findAccountByNumericId(numericId);
    }

    // Lấy thông tin user đầy đủ (String)
    public UserAccount fetchFullAccountById(String userId) {
        return lookupAccountById(userId);
    }

    // Lấy user theo username
    public UserAccount queryAccountByUsername(String username) {
        String sql = "SELECT * FROM UserAccount WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy user theo email (chỉ user active)
    public UserAccount getActiveAccountByEmail(String email) {
        String sql = "SELECT * FROM UserAccount WHERE email = ? AND status = 'Active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserAccount(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserAccount mapResultSetToUserAccount(ResultSet rs) throws SQLException {
        UserAccount ua = new UserAccount();
        ua.setId(rs.getString("id"));
        ua.setUsername(rs.getString("username"));
        ua.setPassword(rs.getString("password"));
        ua.setEmail(rs.getString("email"));
        ua.setAvatar_url(rs.getString("avatar_url")); // chú ý đúng tên hàm
        ua.setRole(rs.getString("role"));
        ua.setStatus(rs.getString("status"));
        ua.setCreate_at(rs.getString("create_at"));   // chú ý đúng tên hàm và cột DB
        ua.setPhonenumber(rs.getString("phonenumber"));

        // Có thể null nếu là customer
        try { ua.setBranchId(rs.getObject("branch_id") != null ? rs.getInt("branch_id") : null); } catch (Exception ignore) {}

        // Nếu join thêm các trường bên ngoài bảng UserAccount
        try { ua.setBranchName(rs.getString("branchName")); } catch (Exception ignore) {}
        try { ua.setFullname(rs.getString("full_name")); } catch (Exception ignore) {}
        try { ua.setRank(rs.getString("rank")); } catch (Exception ignore) {}

        return ua;
    }

    // author : hung
    // Content: get useraccount by id follow new constructor
    public boolean insertHotelOwner(UserAccount user) {
        try {
            // 1. Tạo ID mới
            String getMaxIdSql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) AS maxId FROM UserAccount";
            PreparedStatement ps1 = connection.prepareStatement(getMaxIdSql);
            ResultSet rs = ps1.executeQuery();

            String newId = "U001";
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                newId = String.format("U%03d", maxId + 1);
            }

            // 2. Chỉ insert user mới (role = HotelOwner)
            String insertUserSql = "INSERT INTO UserAccount (id, username, password, email, avatar_url, fullname, phonenumber, role) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(insertUserSql);
            ps2.setString(1, newId);
            ps2.setString(2, user.getUsername());
            ps2.setString(3, user.getPassword());
            ps2.setString(4, user.getEmail());
            ps2.setString(5, user.getAvatar_url());
            ps2.setString(6, user.getFullname());
            ps2.setString(7, user.getPhonenumber());
            ps2.setString(8, user.getRole());

            int rows = ps2.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // author : hung
    // Content: insert account for staff
    public boolean insertUser(UserAccount user) {
        String newId = "U001";

        try {
            // 1. Lấy ID lớn nhất hiện tại để sinh ID mới
            String getMaxIdSql = "SELECT MAX(CAST(SUBSTRING(id, 2, LEN(id)) AS INT)) AS maxId FROM UserAccount";
            PreparedStatement ps1 = connection.prepareStatement(getMaxIdSql);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                int maxId = rs.getInt("maxId");
                newId = String.format("U%03d", maxId + 1);
            }

            // 2. Câu lệnh INSERT (chỉ insert các trường do bạn cung cấp)
            String sql = """
            INSERT INTO UserAccount (
                id, username, password, email, avatar_url,
                role, phonenumber, branch_id, fullname
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

            PreparedStatement ps2 = connection.prepareStatement(sql);
            ps2.setString(1, newId);
            ps2.setString(2, user.getUsername());
            ps2.setString(3, user.getPassword());
            ps2.setString(4, user.getEmail());
            ps2.setString(5, user.getAvatar_url());
            ps2.setString(6, user.getRole());
            ps2.setString(7, user.getPhonenumber());

            if (user.getBranchId() != null) {
                ps2.setInt(8, user.getBranchId());
            } else {
                ps2.setNull(8, java.sql.Types.INTEGER);
            }

            ps2.setString(9, user.getFullname());

            int rows = ps2.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // author : hung
    // Content: Check the last account
    public boolean isLastActiveAccountOfRole(String role) {
        String sql = "SELECT COUNT(*) AS total FROM UserAccount WHERE role = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                return total == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // author : hung
    // Content: Check is Admin Or HotelOwner
    public boolean isAdminOrHotelOwner(String userId) {
        String sql = "SELECT role FROM UserAccount WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                return "Admin".equalsIgnoreCase(role) || "HotelOwner".equalsIgnoreCase(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        UserAccountDAO userAccountDAO = new UserAccountDAO();
        List<String> userList = userAccountDAO.getAllStatuses();
        for (String userAccount : userList) {
            System.out.println(userAccount);
        }

//        List<UserAccount> userList = userAccountDAO.getAllUsersAccount();
//        for (UserAccount userAccount : userList) {
//            System.out.println(userAccount);
//        }
    }
}
