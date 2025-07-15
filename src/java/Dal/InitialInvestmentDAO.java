/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.HotelBranch;
import Model.InitialInvestment;
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
public class InitialInvestmentDAO extends DBcontext.DBContext {

    public List<InitialInvestment> getInitialInvestmentsSimple() {
        List<InitialInvestment> investments = new ArrayList<>();
        String sql = "SELECT * FROM InitialInvestment";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InitialInvestment investment = new InitialInvestment(
                        rs.getInt("BranchId"),
                        rs.getDouble("Capital"),
                        rs.getDate("InvestedDate")
                );
                investments.add(investment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    public List<InitialInvestment> getInitialInvestmentsWithHotelBranch() {
        List<InitialInvestment> investments = new ArrayList<>();
        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE b.is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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

                InitialInvestment investment = new InitialInvestment(
                        rs.getInt("id"),
                        rs.getDouble("Capital"),
                        rs.getDate("InvestedDate"),
                        branch
                );

                investments.add(investment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    public List<InitialInvestment> getInitialInvestmentsByBranchId(int branchId) {
        List<InitialInvestment> investments = new ArrayList<>();
        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE b.is_deleted = 0 AND i.BranchId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investments.add(investment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    public List<InitialInvestment> getInitialInvestmentsByDateRange(Date fromDate, Date toDate) {
        List<InitialInvestment> investments = new ArrayList<>();
        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE b.is_deleted = 0 AND i.InvestedDate BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investments.add(investment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    public List<InitialInvestment> getInitialInvestmentsByBranchAndDateRange(int branchId, Date fromDate, Date toDate) {
        List<InitialInvestment> investments = new ArrayList<>();
        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE b.is_deleted = 0 AND i.BranchId = ? AND i.InvestedDate BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, fromDate);
            ps.setDate(3, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investments.add(investment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investments;
    }

    public double getTotalInitialCapital() {
        String sql = "SELECT SUM(Capital) AS TotalCapital FROM InitialInvestment";
        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble("TotalCapital");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalCapitalByBranchId(int branchId) {
        String sql = "SELECT SUM(Capital) AS TotalCapital FROM InitialInvestment WHERE BranchId = ?";
        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("TotalCapital");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalCapitalByDateRange(Date from, Date to) {
        String sql = "SELECT SUM(Capital) AS TotalCapital FROM InitialInvestment WHERE InvestedDate BETWEEN ? AND ?";
        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("TotalCapital");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public double getTotalCapitalByBranchAndDateRange(int branchId, Date from, Date to) {
        String sql = "SELECT SUM(Capital) AS TotalCapital FROM InitialInvestment "
                + "WHERE BranchId = ? AND InvestedDate BETWEEN ? AND ?";
        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setDate(2, from);
            ps.setDate(3, to);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("TotalCapital");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public InitialInvestment getLatestInitialInvestmentByBranchId(int branchId) {
        String sql = "SELECT TOP 1 * FROM InitialInvestment "
                + "WHERE BranchId = ? "
                + "ORDER BY InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new InitialInvestment(
                            rs.getInt("BranchId"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // nếu không có kết quả
    }

    public boolean insertInitialInvestment(InitialInvestment investment) {
        String sql = "INSERT INTO InitialInvestment (BranchId, Capital, InvestedDate) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, investment.getBranchId());
            ps.setDouble(2, investment.getCapital());
            ps.setDate(3, investment.getInvestedDate());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalCapitalByBranchAndDateRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;
        String sql = "SELECT SUM(Capital) AS total FROM InitialInvestment "
                + "WHERE BranchId = ? "
                + "AND InvestedDate >= ? AND InvestedDate <= ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);

            // Tính ngày bắt đầu và kết thúc theo đầu tháng & cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            st.setDate(2, java.sql.Date.valueOf(fromDate));
            st.setDate(3, java.sql.Date.valueOf(toDate));

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public InitialInvestment getInitialInvestmentById(int investmentId) {
        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE b.is_deleted = 0 AND i.id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, investmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

                    return new InitialInvestment(
                            rs.getInt("id"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Trả về null nếu không tìm thấy hoặc có lỗi
    }

    public boolean updateInitialInvestment(InitialInvestment investment) {
        String sql = "UPDATE InitialInvestment SET Capital = ?, InvestedDate = ?, BranchId = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, investment.getCapital());
            ps.setDate(2, investment.getInvestedDate());
            ps.setInt(3, investment.getBranchId());
            ps.setInt(4, investment.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Trả về false nếu có lỗi xảy ra
    }

    public boolean deleteInitialInvestment(int investmentId) {
        String sql = "DELETE FROM InitialInvestment WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, investmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu xóa thành công

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Trả về false nếu có lỗi
    }

    public List<InitialInvestment> getInitialInvestmentsByBranchAndMonthYear(int branchId, int month, int year) {
        List<InitialInvestment> investmentList = new ArrayList<>();

        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE i.BranchId = ? "
                + "AND MONTH(i.InvestedDate) = ? "
                + "AND YEAR(i.InvestedDate) = ? "
                + "AND b.is_deleted = 0 "
                + "ORDER BY i.InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getInt("id"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investmentList.add(investment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investmentList;
    }

    public List<InitialInvestment> getInitialInvestmentsByMonthYear(int month, int year) {
        List<InitialInvestment> investmentList = new ArrayList<>();

        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE MONTH(i.InvestedDate) = ? "
                + "AND YEAR(i.InvestedDate) = ? "
                + "AND b.is_deleted = 0 "
                + "ORDER BY i.InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getInt("id"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investmentList.add(investment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investmentList;
    }

    public double getTotalCapitalByBranchAndMonthYear(int branchId, int month, int year) {
        double totalCapital = 0;

        String sql = "SELECT SUM(Capital) AS total_capital "
                + "FROM InitialInvestment "
                + "WHERE BranchId = ? "
                + "AND MONTH(InvestedDate) = ? "
                + "AND YEAR(InvestedDate) = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalCapital = rs.getDouble("total_capital");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalCapital;
    }

    public LocalDate getLatestInvestmentMonthYear() {
        String sql = "SELECT TOP 1 InvestedDate FROM InitialInvestment ORDER BY InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Date investedDate = rs.getDate("InvestedDate");
                return investedDate.toLocalDate().withDayOfMonth(1); // Trả về ngày đầu tháng của tháng mới nhất
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Không có dữ liệu
    }

    // Hàm mới
    public List<InitialInvestment> getInitialInvestmentsByMonthRange(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        List<InitialInvestment> investmentList = new ArrayList<>();

        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE i.InvestedDate >= ? "
                + "AND i.InvestedDate <= ? "
                + "AND b.is_deleted = 0 "
                + "ORDER BY i.InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Tính ngày đầu tháng và cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getInt("id"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investmentList.add(investment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investmentList;
    }

    public List<InitialInvestment> getInitialInvestmentsByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        List<InitialInvestment> investmentList = new ArrayList<>();

        String sql = "SELECT i.*, b.id AS branch_id, b.name, b.address, b.phone, b.email, b.image_url, b.owner_id, b.manager_id "
                + "FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE i.InvestedDate >= ? "
                + "AND i.InvestedDate <= ? "
                + "AND i.BranchId = ? "
                + "AND b.is_deleted = 0 "
                + "ORDER BY i.InvestedDate DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // Tính ngày đầu tháng và cuối tháng
            LocalDate fromDate = LocalDate.of(yearFrom, monthFrom, 1);
            LocalDate toDate = LocalDate.of(yearTo, monthTo, YearMonth.of(yearTo, monthTo).lengthOfMonth());

            ps.setDate(1, java.sql.Date.valueOf(fromDate));
            ps.setDate(2, java.sql.Date.valueOf(toDate));
            ps.setInt(3, branchId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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

                    InitialInvestment investment = new InitialInvestment(
                            rs.getInt("id"),
                            rs.getDouble("Capital"),
                            rs.getDate("InvestedDate"),
                            branch
                    );

                    investmentList.add(investment);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return investmentList;
    }

    public double getTotalInitialInvestmentByMonthRange(int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Capital) AS total FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE i.InvestedDate >= ? AND i.InvestedDate <= ? "
                + "AND b.is_deleted = 0";

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

    public double getTotalInitialInvestmentByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;

        String sql = "SELECT SUM(Capital) AS total FROM InitialInvestment i "
                + "JOIN HotelBranch b ON i.BranchId = b.id "
                + "WHERE i.InvestedDate >= ? AND i.InvestedDate <= ? "
                + "AND i.BranchId = ? "
                + "AND b.is_deleted = 0";

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

}
