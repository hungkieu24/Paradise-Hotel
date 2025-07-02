package Dal;

import Model.Invoice;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class InvoiceDAO extends DBcontext.DBContext {
    
    public int createInvoice(Invoice invoice) {
        try {
            String sql = "INSERT INTO Invoice (booking_id, total_amount, issued_at) VALUES (?, ?, ?)";
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, invoice.getBookingId());
                ps.setDouble(2, invoice.getTotalAmount());
                ps.setTimestamp(3, invoice.getIssuedAt());
                
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
    
    public Invoice getInvoiceByBookingId(int bookingId) {
        try {
            String sql = "SELECT * FROM Invoice WHERE booking_id = ?";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, bookingId);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Invoice invoice = new Invoice();
                        invoice.setId(rs.getInt("id"));
                        invoice.setBookingId(rs.getInt("booking_id"));
                        invoice.setTotalAmount(rs.getDouble("total_amount"));
                        invoice.setIssuedAt(rs.getTimestamp("issued_at"));
                        invoice.setPdfUrl(rs.getString("pdf_url"));
                        invoice.setImageUrl(rs.getString("image_url"));
                        return invoice;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}