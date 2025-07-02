package Dal;

import Model.VNPayPayment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class VNPayPaymentDAO extends DBcontext.DBContext {
    
    public int createPayment(VNPayPayment payment) {
        try {
            String sql = "INSERT INTO VNPayPayment (booking_id, amount, status, paid_at) " +
                         "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, payment.getBookingId());
                ps.setDouble(2, payment.getAmount());
                ps.setString(3, payment.getStatus());
                ps.setTimestamp(4, payment.getPaidAt());
                
                int result = ps.executeUpdate();
                if (result > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean createTransaction(int paymentId, String txnRef, String paymentMethod, double amount) {
        try {
            String sql = "INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_Amount, vnp_BankCode, created_at) " +
                         "VALUES (?, ?, ?, ?, GETDATE())";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, paymentId);
                ps.setString(2, txnRef);
                ps.setDouble(3, amount);
                ps.setString(4, paymentMethod);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tạo transaction với thông tin staff
     */
    public boolean createTransactionWithStaffInfo(int paymentId, String txnRef, String paymentMethod, 
            double amount, String staffNote) {
        try {
            String sql = "INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_Amount, vnp_BankCode, created_at) " +
                         "VALUES (?, ?, ?, ?, GETDATE())";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, paymentId);
                ps.setString(2, txnRef + " - " + staffNote);
                ps.setDouble(3, amount);
                ps.setString(4, paymentMethod);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}