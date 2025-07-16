/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.BranchMonthlyReport;
import Model.HotelBranch;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungk
 */
public class BranchMonthlyReportDAO extends DBcontext.DBContext {

    public List<BranchMonthlyReport> getBranchMonthlyReportSimple() {
        List<BranchMonthlyReport> reports = new ArrayList<>();
        String sql = "SELECT * FROM BranchMonthlyReport";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BranchMonthlyReport report = new BranchMonthlyReport(
                        rs.getInt("Id"),
                        rs.getInt("BranchId"),
                        rs.getDate("ReportMonth"),
                        rs.getDouble("Revenue"),
                        rs.getDouble("Expenses"),
                        rs.getDouble("Profit"),
                        rs.getDouble("ProfitRate")
                );
                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    public List<BranchMonthlyReport> getBranchMonthlyReportsWithHotelBranch() {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE b.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BranchMonthlyReport report = new BranchMonthlyReport(
                        rs.getInt("Id"),
                        rs.getInt("BranchId"),
                        rs.getDate("ReportMonth"),
                        rs.getDouble("Revenue"),
                        rs.getDouble("Expenses"),
                        rs.getDouble("Profit"),
                        rs.getDouble("ProfitRate")
                );

                HotelBranch branch = new HotelBranch(
                        rs.getInt("branch_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getString("owner_id"),
                        rs.getString("manager_id")
                );

                report.setHotelBranch(branch);
                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    /// cbi bỏ
    public BranchMonthlyReport getLatestBranchMonthlyReportByBranchId(int branchId) {
        String sql = "SELECT TOP 1 * FROM BranchMonthlyReport "
                + "WHERE BranchId = ? "
                + "ORDER BY ReportMonth DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BranchMonthlyReport(
                            rs.getInt("Id"),
                            rs.getInt("BranchId"),
                            rs.getDate("ReportMonth"),
                            rs.getDouble("Revenue"),
                            rs.getDouble("Expenses"),
                            rs.getDouble("Profit"),
                            rs.getDouble("ProfitRate")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Không có báo cáo
    }

    public boolean insertBranchMonthlyReport(BranchMonthlyReport report) {
        String sql = "INSERT INTO BranchMonthlyReport "
                + "(BranchId, ReportMonth, Revenue, Expenses, Profit, ProfitRate) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, report.getBranchId());
            ps.setDate(2, report.getReportMonth());
            ps.setDouble(3, report.getRevenue());
            ps.setDouble(4, report.getExpenses());
            ps.setDouble(5, report.getProfit());
            ps.setDouble(6, report.getProfitRate());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void upsertMonthlyReport(int branchId, LocalDate reportMonth, double revenue, double expenses, double profit, double profitRate) {
        String checkSql = "SELECT Revenue, Expenses, Profit, ProfitRate FROM BranchMonthlyReport WHERE BranchId = ? AND ReportMonth = ?";
        String insertSql = "INSERT INTO BranchMonthlyReport (BranchId, ReportMonth, Revenue, Expenses, Profit, ProfitRate, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        String updateSql = "UPDATE BranchMonthlyReport SET Revenue = ?, Expenses = ?, Profit = ?, ProfitRate = ? WHERE BranchId = ? AND ReportMonth = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, branchId);
            checkStmt.setDate(2, java.sql.Date.valueOf(reportMonth));

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double oldRevenue = rs.getDouble("Revenue");
                double oldExpenses = rs.getDouble("Expenses");
                double oldProfit = rs.getDouble("Profit");
                double oldProfitRate = rs.getDouble("ProfitRate");

                // So sánh giá trị cũ và mới (có thể làm tròn nếu cần để tránh sai số)
                if (Double.compare(oldRevenue, revenue) != 0
                        || Double.compare(oldExpenses, expenses) != 0
                        || Double.compare(oldProfit, profit) != 0
                        || Double.compare(oldProfitRate, profitRate) != 0) {

                    // Dữ liệu thay đổi → Cập nhật
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, revenue);
                        updateStmt.setDouble(2, expenses);
                        updateStmt.setDouble(3, profit);
                        updateStmt.setDouble(4, profitRate);
                        updateStmt.setInt(5, branchId);
                        updateStmt.setDate(6, java.sql.Date.valueOf(reportMonth));
                        updateStmt.executeUpdate();
                    }
                }
                // Nếu dữ liệu giống hệt → Không làm gì
            } else {
                // Chưa có bản ghi → Thêm mới
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, branchId);
                    insertStmt.setDate(2, java.sql.Date.valueOf(reportMonth));
                    insertStmt.setDouble(3, revenue);
                    insertStmt.setDouble(4, expenses);
                    insertStmt.setDouble(5, profit);
                    insertStmt.setDouble(6, profitRate);
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hàm mới
    public List<BranchMonthlyReport> getBranchMonthlyReportsByMonthRangeAllBranches(int monthFrom, int yearFrom, int monthTo, int yearTo, int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id, b.is_deleted "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.ReportMonth >= ? "
                + "AND r.ReportMonth <= ? "
                + "ORDER BY r.ReportMonth DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Tính ngày đầu tháng và ngày cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));
            ps.setInt(3, (page - 1) * pageSize);
            ps.setInt(4, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BranchMonthlyReport report = new BranchMonthlyReport(
                        rs.getInt("Id"),
                        rs.getInt("BranchId"),
                        rs.getDate("ReportMonth"),
                        rs.getDouble("Revenue"),
                        rs.getDouble("Expenses"),
                        rs.getDouble("Profit"),
                        rs.getDouble("ProfitRate")
                );

                HotelBranch branch = new HotelBranch(
                        rs.getInt("branch_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getString("owner_id"),
                        rs.getString("manager_id")
                );

                report.setHotelBranch(branch);
                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    public List<BranchMonthlyReport> getBranchMonthlyReportsByMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo, int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id, b.is_deleted "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.BranchId = ? "
                + "AND r.ReportMonth >= ? "
                + "AND r.ReportMonth <= ? "
                + "ORDER BY r.ReportMonth DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            // Tính ngày đầu tháng và ngày cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(2, java.sql.Date.valueOf(fromDate));
            ps.setDate(3, java.sql.Date.valueOf(toDate));
            ps.setInt(4, (page - 1) * pageSize);
            ps.setInt(5, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BranchMonthlyReport report = new BranchMonthlyReport(
                        rs.getInt("Id"),
                        rs.getInt("BranchId"),
                        rs.getDate("ReportMonth"),
                        rs.getDouble("Revenue"),
                        rs.getDouble("Expenses"),
                        rs.getDouble("Profit"),
                        rs.getDouble("ProfitRate")
                );

                HotelBranch branch = new HotelBranch(
                        rs.getInt("branch_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("image_url"),
                        rs.getString("owner_id"),
                        rs.getString("manager_id")
                );

                report.setHotelBranch(branch);
                reports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reports;
    }

    public int getBranchMonthlyReportCountAllBranches(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS total FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? "
                + "AND ReportMonth <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Tính ngày đầu tháng và cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getBranchMonthlyReportCount(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS total FROM BranchMonthlyReport "
                + "WHERE BranchId = ? "
                + "AND ReportMonth >= ? "
                + "AND ReportMonth <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            // Tính ngày đầu tháng và cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(2, java.sql.Date.valueOf(fromDate));
            ps.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public double getTotalRevenueAllBranches(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Revenue) AS total FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? AND ReportMonth <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalRevenueByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Revenue) AS total FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? AND ReportMonth <= ? "
                + "AND BranchId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));
            ps.setInt(3, branchId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalExpensesAllBranches(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Expenses) AS total FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? AND ReportMonth <= ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalExpensesByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Expenses) AS total FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? AND ReportMonth <= ? "
                + "AND BranchId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));
            ps.setInt(3, branchId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public int[] getEarliestMonthYearInBranchMonthlyReport() {
        int[] result = new int[2]; // {month, year}

        String sql = "SELECT TOP 1 MONTH(ReportMonth) AS month, YEAR(ReportMonth) AS year "
                + "FROM BranchMonthlyReport "
                + "ORDER BY ReportMonth ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                result[0] = rs.getInt("month");
                result[1] = rs.getInt("year");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getMonthlyProfitTrendByBranchAndDateRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE BranchId = ? "
                + "AND ReportMonth >= ? AND ReportMonth <= ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            stmt.setInt(1, branchId);
            stmt.setDate(2, java.sql.Date.valueOf(fromDate));
            stmt.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                monthlyProfits.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Double> getMonthlyProfitTrendAllBranches(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE ReportMonth >= ? AND ReportMonth <= ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                monthlyProfits.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Map<String, Double>> getBranchComparisonDataByDateRange(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();

        String sql = "SELECT hb.name AS BranchName, "
                + "SUM(bmr.Revenue) AS TotalRevenue, "
                + "SUM(bmr.Expenses) AS TotalExpenses, "
                + "SUM(bmr.Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport bmr "
                + "JOIN HotelBranch hb ON bmr.BranchId = hb.id "
                + "WHERE bmr.ReportMonth >= ? AND bmr.ReportMonth <= ? "
                + "GROUP BY hb.name "
                + "ORDER BY hb.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            stmt.setDate(1, java.sql.Date.valueOf(fromDate));
            stmt.setDate(2, java.sql.Date.valueOf(toDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String branchName = rs.getString("BranchName");
                double revenue = rs.getDouble("TotalRevenue");
                double expenses = rs.getDouble("TotalExpenses");
                double profit = rs.getDouble("TotalProfit");

                Map<String, Double> metrics = new HashMap<>();
                metrics.put("revenue", revenue);
                metrics.put("expenses", expenses);
                metrics.put("profit", profit);

                data.put(branchName, metrics);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public Map<String, Map<String, Double>> getBranchIndicatorsByMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();

        String sql = "SELECT FORMAT(ReportMonth, 'yyyy-MM') AS Month, "
                + "SUM(Revenue) AS TotalRevenue, "
                + "SUM(Expenses) AS TotalExpenses, "
                + "SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE BranchId = ? "
                + "AND ReportMonth >= ? AND ReportMonth <= ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            stmt.setInt(1, branchId);
            stmt.setDate(2, java.sql.Date.valueOf(fromDate));
            stmt.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String month = rs.getString("Month");
                double revenue = rs.getDouble("TotalRevenue");
                double expenses = rs.getDouble("TotalExpenses");
                double profit = rs.getDouble("TotalProfit");

                Map<String, Double> metrics = new HashMap<>();
                metrics.put("revenue", revenue);
                metrics.put("expenses", expenses);
                metrics.put("profit", profit);

                data.put(month, metrics);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

}
