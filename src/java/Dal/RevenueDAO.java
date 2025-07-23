/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Revenue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 *
 * @author hungk
 */
public class RevenueDAO extends DBcontext.DBContext {

    public List<Revenue> getRevenueByBranchId(int branchId) {
        List<Revenue> revenueList = new ArrayList<>();
        String sql = "SELECT * FROM Revenue WHERE branch_id = ? ORDER BY revenue_date DESC";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, branchId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Revenue revenue = new Revenue(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("revenue_type"),
                        rs.getDouble("amount"),
                        rs.getDate("revenue_date"),
                        rs.getString("source"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                );
                revenueList.add(revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueList;
    }

    public List<Revenue> getRevenueByBranchAndMonthYear(int branchId, int month, int year) {
        List<Revenue> revenueList = new ArrayList<>();
        String sql = "SELECT * FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND MONTH(revenue_date) = ? "
                + "AND YEAR(revenue_date) = ? "
                + "ORDER BY revenue_date DESC";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, branchId);
            st.setInt(2, month);
            st.setInt(3, year);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Revenue revenue = new Revenue(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("revenue_type"),
                        rs.getDouble("amount"),
                        rs.getDate("revenue_date"),
                        rs.getString("source"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                );
                revenueList.add(revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueList;
    }

    public double getTotalRevenueByBranchAndMonthYear(int branchId, int month, int year) {
        double totalRevenue = 0;
        String sql = "SELECT SUM(amount) AS total FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND MONTH(revenue_date) = ? "
                + "AND YEAR(revenue_date) = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, branchId);
            st.setInt(2, month);
            st.setInt(3, year);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                totalRevenue = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRevenue;
    }

    public List<Revenue> getRevenueBySourceAndMonthYear(int branchId, String source, int month, int year) {
        List<Revenue> revenueList = new ArrayList<>();
        String sql = "SELECT * FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND source = ? "
                + "AND MONTH(revenue_date) = ? "
                + "AND YEAR(revenue_date) = ? "
                + "ORDER BY revenue_date DESC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setString(2, source);
            st.setInt(3, month);
            st.setInt(4, year);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Revenue revenue = new Revenue(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("revenue_type"),
                        rs.getDouble("amount"),
                        rs.getDate("revenue_date"),
                        rs.getString("source"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                );
                revenueList.add(revenue);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueList;
    }

    public double getTotalRevenueBySourceAndMonthYear(int branchId, String source, int month, int year) {
        String sql = "SELECT SUM(amount) AS total FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND source = ? "
                + "AND MONTH(revenue_date) = ? "
                + "AND YEAR(revenue_date) = ?";
        double total = 0;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setString(2, source);
            st.setInt(3, month);
            st.setInt(4, year);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public List<String> getAllRevenueSourcesByBranchId(int branchId) {
        List<String> sources = new ArrayList<>();
        String sql = "SELECT DISTINCT source FROM Revenue WHERE branch_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                sources.add(rs.getString("source"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sources;
    }

    public void exportSystemRevenueDaily() {
        String query = "SELECT id, branch_id, total_price, check_out "
                + "FROM Booking "
                + "WHERE status = 'Completed' AND exported_to_revenue = 0";

        try (PreparedStatement st = connection.prepareStatement(query); ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int branchId = rs.getInt("branch_id");
                double amount = rs.getDouble("total_price");
                Date checkoutDate = rs.getDate("check_out");

                // Thêm vào Revenue
                String insertSql = "INSERT INTO Revenue "
                        + "(branch_id, revenue_type, amount, revenue_date, source, description, created_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                    insert.setInt(1, branchId);
                    insert.setString(2, "Online Booking");
                    insert.setDouble(3, amount);
                    insert.setDate(4, checkoutDate);
                    insert.setString(5, "SYSTEM");
                    insert.setString(6, "Booking ID: " + bookingId);
                    insert.setString(7, "SYSTEM");
                    insert.executeUpdate();
                }

                // Cập nhật Booking đã xuất revenue
                String updateSql = "UPDATE Booking SET exported_to_revenue = 1 WHERE id = ?";
                try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                    update.setInt(1, bookingId);
                    update.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Revenue getRevenueById(int id) {
        String sql = "SELECT * FROM Revenue WHERE id = ?";
        Revenue revenue = null;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                revenue = new Revenue(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("revenue_type"),
                        rs.getDouble("amount"),
                        rs.getDate("revenue_date"),
                        rs.getString("source"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenue;
    }

    public boolean insertRevenue(Revenue revenue) {
        String sql = "INSERT INTO Revenue (branch_id, revenue_type, amount, revenue_date, source, description, created_by, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, revenue.getBranch_id());
            st.setString(2, revenue.getRevenue_type());
            st.setDouble(3, revenue.getAmount());
            st.setDate(4, new java.sql.Date(revenue.getRevenue_date().getTime()));
            st.setString(5, revenue.getSource());
            st.setString(6, revenue.getDescription());
            st.setString(7, revenue.getCreated_by());

            int rows = st.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRevenue(Revenue revenue) {
        String sql = "UPDATE Revenue SET revenue_type = ?, amount = ?, revenue_date = ?, description = ? "
                + "WHERE id = ? AND branch_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, revenue.getRevenue_type());
            st.setDouble(2, revenue.getAmount());
            st.setDate(3, new java.sql.Date(revenue.getRevenue_date().getTime()));
            st.setString(4, revenue.getDescription());
            st.setInt(5, revenue.getId());
            st.setInt(6, revenue.getBranch_id());
            int rows = st.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRevenueById(int id) {
        String sql = "DELETE FROM Revenue WHERE id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            int rows = st.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFieldExists(String fieldName, String value, Integer excludeId) {
        String sql = "SELECT 1 FROM Revenue WHERE " + fieldName + " = ?" + (excludeId != null ? " AND id != ?" : "");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalRevenueByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double totalRevenue = 0;
        String sql = "SELECT SUM(amount) AS total FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND revenue_date >= ? AND revenue_date <= ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);

            // Tính ngày đầu và ngày cuối
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            st.setDate(2, java.sql.Date.valueOf(fromDate));
            st.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                totalRevenue = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRevenue;
    }

    public List<Revenue> getRevenueByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        List<Revenue> revenueList = new ArrayList<>();
        String sql = "SELECT * FROM Revenue "
                + "WHERE branch_id = ? "
                + "AND revenue_date BETWEEN ? AND ? "
                + "ORDER BY revenue_date DESC";

        try {
            // Tính ngày bắt đầu: YYYY-MM-01
            LocalDate startDate = LocalDate.of(yearFrom, monthFrom, 1);

            // Tính ngày kết thúc: ngày cuối cùng của thángTo
            YearMonth ymEnd = YearMonth.of(yearTo, monthTo);
            LocalDate endDate = ymEnd.atEndOfMonth();

            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, branchId);
            st.setDate(2, Date.valueOf(startDate));
            st.setDate(3, Date.valueOf(endDate));

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Revenue revenue = new Revenue(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("revenue_type"),
                        rs.getDouble("amount"),
                        rs.getDate("revenue_date"),
                        rs.getString("source"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getTimestamp("created_at")
                );
                revenueList.add(revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenueList;
    }

    public static void main(String[] args) {
        RevenueDAO aO = new RevenueDAO();
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue(); // từ 1 đến 12
        int currentYear = today.getYear();
        int monthTo = currentMonth;
        int yearTo = currentYear;

        // Lấy total revenue theo tháng hiện tại
        double totalRevenue = aO.getTotalRevenueByBranchAndMonthRange(1, currentMonth, currentYear, monthTo, yearTo);
        System.out.println(totalRevenue);
    }
}
