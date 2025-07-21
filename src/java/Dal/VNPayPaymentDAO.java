package Dal;

import Model.VNPayPayment;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class VNPayPaymentDAO extends DBcontext.DBContext {

    public VNPayPaymentDAO() {
        super();
    }

    public int createPayment(VNPayPayment payment) {
        try {
            String sql = "INSERT INTO VNPayPayment (booking_id, amount, status, paid_at) "
                    + "VALUES (?, ?, ?, ?)";

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

    public void createTransaction(int paymentId, String vnpTxnRef, String vnpTransactionNo,
            String vnpBankCode, String vnpPayDate) {

        // KIỂM TRA DUPLICATE TRƯỚC KHI INSERT
        String checkSql = "SELECT COUNT(*) FROM VNPayTransaction WHERE payment_id = ? AND vnp_TxnRef = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, paymentId);
            checkPs.setString(2, vnpTxnRef);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return; // SKIP nếu đã tồn tại
            }
        } catch (Exception e) {
            System.err.println("Error checking duplicate transaction: " + e.getMessage());
        }

        // CHUYỂN ĐỔI vnpPayDate từ yyyyMMddHHmmss sang DATETIME
        String formattedPayDate = null;
        if (vnpPayDate != null && vnpPayDate.length() == 14) {
            try {
                String year = vnpPayDate.substring(0, 4);
                String month = vnpPayDate.substring(4, 6);
                String day = vnpPayDate.substring(6, 8);
                String hour = vnpPayDate.substring(8, 10);
                String minute = vnpPayDate.substring(10, 12);
                String second = vnpPayDate.substring(12, 14);

                formattedPayDate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            } catch (Exception e) {
                System.err.println("Error formatting PayDate: " + e.getMessage());
                formattedPayDate = null;
            }
        }

        String sql = "INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_TransactionNo, "
                + "vnp_ResponseCode, vnp_Amount, vnp_BankCode, vnp_CardType, vnp_SecureHash, "
                + "is_refunded, created_at, vnp_PayDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setString(2, vnpTxnRef);
            ps.setString(3, vnpTransactionNo);
            ps.setString(4, "00"); // SUCCESS response code
            ps.setDouble(5, 0); // Amount sẽ được set từ PaymentResultServlet
            ps.setString(6, vnpBankCode);
            ps.setString(7, ""); // CardType sẽ được set từ PaymentResultServlet
            ps.setString(8, ""); // SecureHash sẽ được set từ PaymentResultServlet
            ps.setBoolean(9, false); // is_refunded = false

            if (formattedPayDate != null) {
                ps.setString(10, formattedPayDate);
            } else {
                ps.setNull(10, java.sql.Types.TIMESTAMP);
            }

            int result = ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error creating VNPayTransaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tạo transaction với thông tin staff
     */
    public boolean createTransactionWithStaffInfo(int paymentId, String txnRef, String paymentMethod,
            double amount, String staffNote) {
        try {
            String sql = "INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_Amount, vnp_BankCode, created_at) "
                    + "VALUES (?, ?, ?, ?, GETDATE())";

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

    public boolean refundPaymentByBookingId(int bookingId) {
        try {

            String sql = "UPDATE VNPayPayment SET status = 'Refunded' WHERE booking_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);

                int rowsAffected = ps.executeUpdate();
                boolean result = rowsAffected > 0;
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error updating payment refund status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public VNPayTransactionInfo getTransactionInfoByBookingId(int bookingId) {
        try {
            // Lấy từ VNPayTransaction với vnp_TransactionNo
            String sql = "SELECT vt.vnp_TxnRef, vt.vnp_TransactionNo, "
                    + "FORMAT(vp.paid_at, 'yyyyMMddHHmmss') as pay_date, vp.amount "
                    + "FROM VNPayPayment vp "
                    + "JOIN VNPayTransaction vt ON vp.id = vt.payment_id "
                    + "WHERE vp.booking_id = ? AND vp.status = 'Completed'";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                VNPayTransactionInfo info = new VNPayTransactionInfo();
                info.vnpTxnRef = rs.getString("vnp_TxnRef");
                info.vnpTransactionNo = rs.getString("vnp_TransactionNo");
                info.payDate = rs.getString("pay_date");
                info.amount = rs.getDouble("amount");


                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePaymentStatus(int bookingId, String status) {
        String sql = "UPDATE VNPayPayment SET status = ? WHERE booking_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasExistingPayment(int bookingId) {
        String sql = "SELECT COUNT(*) FROM VNPayPayment WHERE booking_id = ? AND status = 'Completed'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAlreadyRefunded(int bookingId) {
        try {
            String sql = "SELECT status FROM VNPayPayment WHERE booking_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return "Refunded".equals(rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if payment was made more than 1 day ago
     * @param bookingId
     * @return true if payment is older than 1 day, false otherwise
     */
    public boolean isPaymentOlderThanOneDay(int bookingId) {
        try {
            String sql = "SELECT paid_at FROM VNPayPayment WHERE booking_id = ? AND status = 'Completed'";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp paidAt = rs.getTimestamp("paid_at");
                if (paidAt != null) {
                    // Calculate 1 day (24 hours) from payment time
                    long oneDayInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
                    long currentTime = System.currentTimeMillis();
                    long paymentTime = paidAt.getTime();

                    boolean isOlderThanOneDay = (currentTime - paymentTime) > oneDayInMillis;



                    return isOlderThanOneDay;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Default to false if no payment found or error
    }

// Inner class for transaction info
    public static class VNPayTransactionInfo {

        public String vnpTxnRef;
        public String vnpTransactionNo;
        public String payDate;
        public double amount;
    }

    public void createTransactionComplete(int paymentId, String vnpTxnRef, String vnpTransactionNo,
            String vnpResponseCode, double amount, String vnpBankCode, String vnpSecureHash, String vnpPayDate) {

        // KIỂM TRA DUPLICATE
        String checkSql = "SELECT COUNT(*) FROM VNPayTransaction WHERE payment_id = ? AND vnp_TxnRef = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setInt(1, paymentId);
            checkPs.setString(2, vnpTxnRef);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        } catch (Exception e) {
            System.err.println("Error checking duplicate: " + e.getMessage());
        }

        // FORMAT PayDate
        String formattedPayDate = formatVNPayDate(vnpPayDate);

        String sql = "INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_TransactionNo, "
                + "vnp_ResponseCode, vnp_Amount, vnp_BankCode, vnp_SecureHash, "
                + "is_refunded, created_at, vnp_PayDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, GETDATE(), ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.setString(2, vnpTxnRef);
            ps.setString(3, vnpTransactionNo);
            ps.setString(4, vnpResponseCode);
            ps.setDouble(5, amount);
            ps.setString(6, vnpBankCode);
            ps.setString(7, vnpSecureHash);

            if (formattedPayDate != null) {
                ps.setTimestamp(8, java.sql.Timestamp.valueOf(formattedPayDate));
            } else {
                ps.setNull(8, java.sql.Types.TIMESTAMP);
            }

            int result = ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("❌ Error creating transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatVNPayDate(String vnpPayDate) {
        if (vnpPayDate == null || vnpPayDate.length() != 14) {
            return null;
        }

        try {
            String formatted = vnpPayDate.substring(0, 4) + "-"
                    + vnpPayDate.substring(4, 6) + "-"
                    + vnpPayDate.substring(6, 8) + " "
                    + vnpPayDate.substring(8, 10) + ":"
                    + vnpPayDate.substring(10, 12) + ":"
                    + vnpPayDate.substring(12, 14);
            return formatted;
        } catch (Exception e) {
            System.err.println("Error formatting PayDate: " + vnpPayDate + " - " + e.getMessage());
            return null;
        }
    }

    public boolean updateTransactionRefundStatus(int bookingId) {
        try {
            String sql = "UPDATE VNPayTransaction SET is_refunded = 1 "
                    + "WHERE payment_id IN (SELECT id FROM VNPayPayment WHERE booking_id = ?)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);

                int rowsAffected = ps.executeUpdate();
                boolean result = rowsAffected > 0;
                return result;
            }
        } catch (Exception e) {
            System.err.println("Error updating transaction refund status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
