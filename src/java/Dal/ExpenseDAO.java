/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.Expense;
import java.sql.Timestamp;
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
public class ExpenseDAO extends DBcontext.DBContext {

    public List<Expense> getExpenseByBranchAndMonthYear(int branchId, int month, int year) {
        List<Expense> expenseList = new ArrayList<>();
        String sql = "SELECT * FROM Expense "
                + "WHERE branch_id = ? "
                + "AND MONTH(expense_date) = ? "
                + "AND YEAR(expense_date) = ? "
                + "ORDER BY expense_date DESC";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setInt(2, month);
            st.setInt(3, year);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("expense_type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getDate("expense_date"),
                        rs.getString("created_by")
                );
                expenseList.add(expense);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expenseList;
    }

    public double getTotalExpenseByBranchAndMonthYear(int branchId, int month, int year) {
        String sql = "SELECT SUM(amount) AS total FROM Expense "
                + "WHERE branch_id = ? "
                + "AND MONTH(expense_date) = ? "
                + "AND YEAR(expense_date) = ?";
        double total = 0;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);
            st.setInt(2, month);
            st.setInt(3, year);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public Expense getExpenseById(int id) {
        String sql = "SELECT * FROM Expense WHERE id = ?";
        Expense expense = null;

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                expense = new Expense(
                        rs.getInt("id"),
                        rs.getInt("branch_id"),
                        rs.getString("expense_type"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getDate("expense_date"),
                        rs.getString("created_by")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return expense;
    }

    public boolean insertExpense(Expense expense) {
        String sql = "INSERT INTO Expense (branch_id, expense_type, amount, description, expense_date, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, expense.getBranch_id());
            st.setString(2, expense.getExpense_type());
            st.setDouble(3, expense.getAmount());
            st.setString(4, expense.getDescription());
            st.setDate(5, expense.getExpense_date());
            st.setString(6, expense.getCreated_by());

            int rows = st.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE Expense SET expense_type = ?, amount = ?, description = ?, expense_date = ? "
                + "WHERE id = ? AND branch_id = ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, expense.getExpense_type());
            st.setDouble(2, expense.getAmount());
            st.setString(3, expense.getDescription());
            st.setDate(4, expense.getExpense_date());
            st.setInt(5, expense.getId());
            st.setInt(6, expense.getBranch_id());

            int rows = st.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExpenseById(int id) {
        String sql = "DELETE FROM Expense WHERE id = ?";

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
        String sql = "SELECT 1 FROM Expense WHERE " + fieldName + " = ?" + (excludeId != null ? " AND id != ?" : "");
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

    public double getTotalExpenseByBranchAndMonthRange(int branchId, int monthFrom, int yearFrom, int monthTo, int yearTo) {
        double total = 0;
        String sql = "SELECT SUM(amount) AS total FROM Expense "
                + "WHERE branch_id = ? "
                + "AND expense_date >= ? AND expense_date <= ?";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, branchId);

            // Tính ngày bắt đầu và kết thúc
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

}
