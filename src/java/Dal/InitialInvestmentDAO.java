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

    public double getTotalCapitalByBranch(int branchId) {
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

}
