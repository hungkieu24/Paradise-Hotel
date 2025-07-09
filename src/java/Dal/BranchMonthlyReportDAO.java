/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.BranchMonthlyReport;
import Model.HotelBranch;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public List<BranchMonthlyReport> getListBranchMonthlyReportByPage(int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE b.is_deleted = 0 "
                + "ORDER BY r.ReportMonth DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);

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

    public int getTotalBranchMonthlyReportByBranchId(int branchId) {
        String sql = "SELECT COUNT(*) FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.BranchId = ?"; // ❌ Không lọc is_deleted

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public List<BranchMonthlyReport> getBranchMonthlyReportsByBranchId(int branchId, int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.BranchId = ? "
                + "ORDER BY r.ReportMonth DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"; // ❌ Không lọc is_deleted

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);

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

    public int getTotalBranchMonthlyReportByBranchIdAndMonthRange(int branchId, Date fromMonth, Date toMonth) {
        String sql = "SELECT COUNT(*) FROM BranchMonthlyReport "
                + "WHERE BranchId = ? AND ReportMonth BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setDate(2, fromMonth);
            stmt.setDate(3, toMonth);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public List<BranchMonthlyReport> getBranchMonthlyReportsByBranchIdAndMonthRange(int branchId, Date fromMonth, Date toMonth, int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id, b.is_deleted "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.BranchId = ? AND r.ReportMonth BETWEEN ? AND ? "
                + "ORDER BY r.ReportMonth DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, fromMonth);
            ps.setDate(3, toMonth);
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

    public int getTotalBranchMonthlyReportByMonthRange(int branchId, Date fromMonth, Date toMonth) {
        String sql = "SELECT COUNT(*) FROM BranchMonthlyReport "
                + "WHERE ReportMonth BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, fromMonth);
            stmt.setDate(2, toMonth);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public List<BranchMonthlyReport> getBranchMonthlyReportsByMonthRange(int branchId, Date fromMonth, Date toMonth, int page, int pageSize) {
        List<BranchMonthlyReport> reports = new ArrayList<>();

        String sql = "SELECT r.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id, b.is_deleted "
                + "FROM BranchMonthlyReport r "
                + "JOIN HotelBranch b ON r.BranchId = b.id "
                + "WHERE r.ReportMonth BETWEEN ? AND ? "
                + "ORDER BY r.ReportMonth DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, fromMonth);
            ps.setDate(2, toMonth);
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

    public Map<String, Double> getSystemTotals(Date fromMonth, Date toMonth) {
        Map<String, Double> result = new HashMap<>();

        String reportSql = "SELECT SUM(Revenue) AS totalRevenue, SUM(Expenses) AS totalExpenses "
                + "FROM BranchMonthlyReport WHERE ReportMonth BETWEEN ? AND ?";

        String capitalSql = "SELECT SUM(Capital) AS totalCapital FROM InitialInvestment";

        double revenue = 0;
        double expenses = 0;
        double capital = 0;

        try {
            // 1. Tính doanh thu & chi phí
            PreparedStatement ps1 = connection.prepareStatement(reportSql);
            ps1.setDate(1, fromMonth);
            ps1.setDate(2, toMonth);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                revenue = rs1.getDouble("totalRevenue");
                expenses = rs1.getDouble("totalExpenses");
            }

            // 2. Lấy tổng vốn đầu tư ban đầu từ InitialInvestment
            PreparedStatement ps2 = connection.prepareStatement(capitalSql);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                capital = rs2.getDouble("totalCapital");
            }

            double netProfit = revenue - expenses;
            double profitMargin = (capital != 0) ? (netProfit / capital) * 100 : 0;

            result.put("Revenue", revenue);
            result.put("Expenses", expenses);
            result.put("Capital", capital);
            result.put("NetProfit", netProfit);
            result.put("ProfitMargin", profitMargin);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getTotalsByBranch(int branchId) {
        Map<String, Double> result = new HashMap<>();

        String reportSql = "SELECT SUM(Revenue) AS totalRevenue, SUM(Expenses) AS totalExpenses "
                + "FROM BranchMonthlyReport WHERE BranchId = ?";

        String capitalSql = "SELECT Capital FROM InitialInvestment WHERE BranchId = ?";

        try {
            PreparedStatement ps1 = connection.prepareStatement(reportSql);
            ps1.setInt(1, branchId);
            ResultSet rs1 = ps1.executeQuery();

            double revenue = 0, expenses = 0, capital = 0;
            if (rs1.next()) {
                revenue = rs1.getDouble("totalRevenue");
                expenses = rs1.getDouble("totalExpenses");
            }

            PreparedStatement ps2 = connection.prepareStatement(capitalSql);
            ps2.setInt(1, branchId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                capital = rs2.getDouble("Capital");
            }

            double netProfit = revenue - expenses;
            double profitMargin = (capital != 0) ? (netProfit / capital) * 100 : 0;

            result.put("Revenue", revenue);
            result.put("Expenses", expenses);
            result.put("Capital", capital);
            result.put("NetProfit", netProfit);
            result.put("ProfitMargin", profitMargin);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getTotalsByBranchAndDateRange(int branchId, Date fromMonth, Date toMonth) {
        Map<String, Double> result = new HashMap<>();

        String reportSql = "SELECT SUM(Revenue) AS totalRevenue, SUM(Expenses) AS totalExpenses "
                + "FROM BranchMonthlyReport WHERE BranchId = ? AND ReportMonth BETWEEN ? AND ?";

        String capitalSql = "SELECT Capital FROM InitialInvestment WHERE BranchId = ?";

        try {
            PreparedStatement ps1 = connection.prepareStatement(reportSql);
            ps1.setInt(1, branchId);
            ps1.setDate(2, fromMonth);
            ps1.setDate(3, toMonth);
            ResultSet rs1 = ps1.executeQuery();

            double revenue = 0, expenses = 0, capital = 0;
            if (rs1.next()) {
                revenue = rs1.getDouble("totalRevenue");
                expenses = rs1.getDouble("totalExpenses");
            }

            PreparedStatement ps2 = connection.prepareStatement(capitalSql);
            ps2.setInt(1, branchId);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                capital = rs2.getDouble("Capital");
            }

            double netProfit = revenue - expenses;
            double profitMargin = (capital != 0) ? (netProfit / capital) * 100 : 0;

            result.put("Revenue", revenue);
            result.put("Expenses", expenses);
            result.put("Capital", capital);
            result.put("NetProfit", netProfit);
            result.put("ProfitMargin", profitMargin);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getSystemTotals() {
        Map<String, Double> result = new HashMap<>();

        String reportSql = "SELECT SUM(Revenue) AS totalRevenue, SUM(Expenses) AS totalExpenses FROM BranchMonthlyReport";
        String capitalSql = "SELECT SUM(Capital) AS totalCapital FROM InitialInvestment";

        double revenue = 0;
        double expenses = 0;
        double capital = 0;

        try {
            // Tổng Revenue + Expenses
            PreparedStatement ps1 = connection.prepareStatement(reportSql);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                revenue = rs1.getDouble("totalRevenue");
                expenses = rs1.getDouble("totalExpenses");
            }

            // Tổng vốn đầu tư ban đầu
            PreparedStatement ps2 = connection.prepareStatement(capitalSql);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                capital = rs2.getDouble("totalCapital");
            }

            double netProfit = revenue - expenses;
            double profitMargin = (capital != 0) ? (netProfit / capital) * 100 : 0;

            result.put("Revenue", revenue);
            result.put("Expenses", expenses);
            result.put("Capital", capital);
            result.put("NetProfit", netProfit);
            result.put("ProfitMargin", profitMargin);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getMonthlyProfitTrend() {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT TOP 6 FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            TreeMap<String, Double> reverseSorted = new TreeMap<>();
            while (rs.next()) {
                reverseSorted.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
            // Đảo lại để tháng từ cũ → mới
            monthlyProfits.putAll(reverseSorted);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Double> getMonthlyProfitTrendByBranch(int branchId) {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT TOP 6 FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE BranchId = ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            TreeMap<String, Double> reverseSorted = new TreeMap<>();
            while (rs.next()) {
                reverseSorted.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
            monthlyProfits.putAll(reverseSorted);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Double> getMonthlyProfitTrendByDateRange(Date fromDate, Date toDate) {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE ReportMonth BETWEEN ? AND ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                monthlyProfits.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Double> getMonthlyProfitTrendByBranchAndDateRange(int branchId, Date fromDate, Date toDate) {
        Map<String, Double> monthlyProfits = new LinkedHashMap<>();

        String sql = "SELECT FORMAT(ReportMonth, 'yyyy-MM') AS Month, SUM(Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport "
                + "WHERE BranchId = ? AND ReportMonth BETWEEN ? AND ? "
                + "GROUP BY FORMAT(ReportMonth, 'yyyy-MM') "
                + "ORDER BY Month";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, branchId);
            stmt.setDate(2, fromDate);
            stmt.setDate(3, toDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                monthlyProfits.put(rs.getString("Month"), rs.getDouble("TotalProfit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyProfits;
    }

    public Map<String, Map<String, Double>> getBranchComparisonData() {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();

        String sql = "SELECT hb.name AS BranchName, "
                + "SUM(bmr.Revenue) AS TotalRevenue, "
                + "SUM(bmr.Expenses) AS TotalExpenses, "
                + "SUM(bmr.Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport bmr "
                + "JOIN HotelBranch hb ON bmr.BranchId = hb.id "
                + "GROUP BY hb.name "
                + "ORDER BY hb.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

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

    public Map<String, Map<String, Double>> getBranchComparisonDataByDateRange(Date fromDate, Date toDate) {
        Map<String, Map<String, Double>> data = new LinkedHashMap<>();

        String sql = "SELECT hb.name AS BranchName, "
                + "SUM(bmr.Revenue) AS TotalRevenue, "
                + "SUM(bmr.Expenses) AS TotalExpenses, "
                + "SUM(bmr.Profit) AS TotalProfit "
                + "FROM BranchMonthlyReport bmr "
                + "JOIN HotelBranch hb ON bmr.BranchId = hb.id "
                + "WHERE bmr.ReportMonth BETWEEN ? AND ? "
                + "GROUP BY hb.name "
                + "ORDER BY hb.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, fromDate);
            stmt.setDate(2, toDate);

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

    public int getReportMonthRange() {
        String sql = "SELECT MIN(ReportMonth) AS MinMonth, MAX(ReportMonth) AS MaxMonth FROM BranchMonthlyReport";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Date minDate = rs.getDate("MinMonth");
                Date maxDate = rs.getDate("MaxMonth");

                if (minDate != null && maxDate != null) {
                    int totalMonths = getMonthDifference(minDate, maxDate) + 1;

                    return totalMonths;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getReportMonthRangeByBranch(int branchId) {
        String sql = "SELECT MIN(ReportMonth) AS MinMonth, MAX(ReportMonth) AS MaxMonth "
                + "FROM BranchMonthlyReport WHERE BranchId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date minDate = rs.getDate("MinMonth");
                    Date maxDate = rs.getDate("MaxMonth");

                    if (minDate != null && maxDate != null) {
                        int totalMonths = getMonthDifference(minDate, maxDate) + 1;

                        return totalMonths;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getReportMonthRangeByBranchAndDate(int branchId, Date fromDate, Date toDate) {
        if (branchId <= 0 || fromDate == null || toDate == null) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM BranchMonthlyReport "
                + "WHERE BranchId = ? AND ReportMonth BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    int totalMonths = getMonthDifference(fromDate, toDate) + 1;
                    return totalMonths;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getMonthDifference(Date start, Date end) {
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calStart.setTime(start);
        calEnd.setTime(end);

        int yearDiff = calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
        int monthDiff = calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);

        return yearDiff * 12 + monthDiff;
    }

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

}
