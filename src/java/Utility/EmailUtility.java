/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import Model.Booking;
import Model.BookingRoomType;
import Model.Room;
import Model.UserAccount;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author hungk
 */
public class EmailUtility {

    public static void sendEmail(String toEmail, String subject, String messageText) throws Exception {
        final String fromEmail = "hung70919@gmail.com"; // Thay bằng email của bạn
        final String password = "swav xtfi qiqe oeqz"; // App Password của Gmail

        // Cấu hình thông số SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Tạo session gửi mail
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Tạo nội dung email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Nội dung HTML của email (dùng UTF-8 để hiển thị đúng tiếng Việt)
        String htmlContent = "<html>"
                + "<body>"
                + "<p>Chào bạn,</p>"
                + "<p>Mã xác nhận của bạn là: <strong style='color:blue;'>" + messageText + "</strong></p>"
                + "<p style='color : red;'>Mã của bạn tồn tại trong 1 phút. Vui lòng không chia sẻ mã này với người khác!</p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // Gửi email
        Transport.send(message);

    }

    public static void sendResetPasswordEmail(String toEmail, String subject, String messageText) throws Exception {
        final String fromEmail = "hung70919@gmail.com"; // Thay bằng email của bạn
        final String password = "swav xtfi qiqe oeqz"; // App Password của Gmail

        // Cấu hình thông số SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Tạo session gửi mail
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Tạo nội dung email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Nội dung HTML của email (dùng UTF-8 để hiển thị đúng tiếng Việt)
        String htmlContent = "<html>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "<p>Xin chào,</p>"
                + "<p>Admin đã đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "<p>Mật khẩu mới của bạn là: <strong style='color:blue; font-size: 16px;'>" + messageText + "</strong></p>"
                + "<p style='color: red;'>Vui lòng không chia sẻ mật khẩu này với người khác.</p>"
                + "<p>Sau khi đăng nhập, hãy đổi mật khẩu để đảm bảo an toàn cho tài khoản của bạn.</p>"
                + "<br>"
                + "<p>Trân trọng,</p>"
                + "<p><strong>Pradise Hotel</strong></p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // Gửi email
        Transport.send(message);

    }
  
  public static void sendRefundEmail(String toEmail, String subject, String htmlContent) throws Exception {
        final String fromEmail = "hung70919@gmail.com";
        final String password = "swav xtfi qiqe oeqz";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    public static void sendInvoice(String toEmail, String subject,
            String messageText, String totalAmount,
            List<Room> bookingRoomList, UserAccount user,
            Booking bookingCurrent, List<BookingRoomType> bookingRoomTypes
    ) throws Exception {
        final String fromEmail = "hung70919@gmail.com"; // Thay bằng email của bạn
        final String password = "swav xtfi qiqe oeqz"; // App Password của Gmail

        // Cấu hình thông số SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Tạo session gửi mail
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Format currency và date
        NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Lấy thông tin membership
        String rank = user.getRank() != null ? user.getRank() : "Member";
        String membershipClass = "membership-member";
        String membershipColor = "#6c757d";
        if ("Silver".equalsIgnoreCase(rank)) {
            membershipClass = "membership-silver";
            membershipColor = "#6c757d";
        } else if ("Gold".equalsIgnoreCase(rank)) {
            membershipClass = "membership-gold";
            membershipColor = "#ffc107";
        } else if ("VIP".equalsIgnoreCase(rank)) {
            membershipClass = "membership-vip";
            membershipColor = "#dc3545";
        }

        // Chỉ chia nhỏ tổng số tiền (chia 1000, bớt 3 số 0)
        String totalAmountShort = "";
        try {
            double total = Double.parseDouble(totalAmount.replaceAll("[^\\d.]", ""));
            totalAmountShort = currencyFormat.format(total / 1000);
        } catch (Exception e) {
            totalAmountShort = totalAmount;
        }

        // Tạo nội dung email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Nội dung HTML của email
        String htmlContent = "<!DOCTYPE html>"
                + "<html lang='vi'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Hóa đơn thanh toán - Paradise Hotel</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; color: #333; line-height: 1.6; }"
                + ".container { max-width: 800px; margin: 0 auto; background-color: white; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }"
                + ".header { background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%); color: white; padding: 40px 30px; text-align: center; }"
                + ".header h1 { font-size: 2.5rem; margin-bottom: 10px; font-weight: 300; }"
                + ".header p { font-size: 1.1rem; opacity: 0.9; }"
                + ".content { padding: 30px; }"
                + ".booking-header { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 8px; padding: 25px; margin-bottom: 25px; }"
                + ".booking-title { font-size: 1.4rem; color: #2c3e50; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }"
                + ".booking-details { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }"
                + ".detail-item { display: flex; align-items: center; gap: 8px; margin-bottom: 12px; }"
                + ".detail-label { font-weight: 600; min-width: 100px; color: #495057; }"
                + ".detail-value { color: #2c3e50; font-weight: 700; }"
                + ".status-badge { background: #d4edda; color: #155724; padding: 6px 16px; border-radius: 20px; font-size: 0.9rem; font-weight: 600; }"
                + ".customer-section { background: #fff; border: 1px solid #e9ecef; border-radius: 8px; padding: 25px; margin-bottom: 25px; }"
                + ".section-title { font-size: 1.2rem; color: #2c3e50; margin-bottom: 20px; font-weight: 600; }"
                + ".customer-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }"
                + ".membership-badge { padding: 8px 16px; border-radius: 20px; font-size: 0.9rem; font-weight: 600; color: white; background: " + membershipColor + "; }"
                + ".room-types-section { background: #fff; border: 1px solid #e9ecef; border-radius: 8px; padding: 25px; margin-bottom: 25px; }"
                + ".room-table { width: 100%; border-collapse: collapse; margin-top: 15px; }"
                + ".room-table th { background: #f8f9fa; padding: 15px; text-align: left; font-weight: 600; color: #495057; border-bottom: 2px solid #e9ecef; }"
                + ".room-table td { padding: 15px; border-bottom: 1px solid #f1f3f4; }"
                + ".quantity-badge { background: #007bff; color: white; padding: 6px 12px; border-radius: 20px; font-weight: 600; }"
                + ".price-text { font-weight: 600; color: #2c3e50; }"
                + ".subtotal-text { font-weight: 700; color: #28a745; font-size: 1.1rem; }"
                + ".payment-summary { background: #fff; border: 1px solid #e9ecef; border-radius: 8px; padding: 25px; margin-bottom: 25px; }"
                + ".total-section { text-align: center; padding: 20px; }"
                + ".total-amount { font-size: 2.5rem; font-weight: 700; color: #1976d2; margin: 15px 0; }"
                + ".summary-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 30px; align-items: start; }"
                + ".summary-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px solid #f1f3f4; }"
                + ".summary-item:last-child { border-bottom: none; }"
                + ".summary-label { color: #495057; font-weight: 600; }"
                + ".summary-value { font-weight: 700; color: #2c3e50; }"
                + ".summary-positive { color: #28a745; }"
                + ".amount-to-pay { background: linear-gradient(135deg, #e3f2fd, #f3e5f5); border: 2px solid #1976d2; border-radius: 10px; padding: 25px; text-align: center; }"
                + ".amount-title { font-size: 1.2rem; color: #1976d2; margin-bottom: 15px; font-weight: 600; }"
                + ".amount-value { font-size: 2.2rem; font-weight: 700; color: #1976d2; margin-bottom: 10px; }"
                + ".amount-subtitle { color: #6c757d; font-size: 1rem; }"
                + ".footer { background: linear-gradient(135deg, #2c3e50, #34495e); color: white; padding: 30px; text-align: center; }"
                + ".footer h3 { margin-bottom: 15px; }"
                + ".footer p { margin: 8px 0; opacity: 0.9; }"
                + ".divider { height: 3px; background: linear-gradient(to right, #2c3e50, #3498db, #2c3e50); margin: 30px 0; }"
                + ".thank-you { background: #f8f9fa; padding: 25px; text-align: center; border-radius: 8px; margin: 25px 0; }"
                + ".icon { margin-right: 8px; }"
                + "@media (max-width: 600px) {"
                + "  .booking-details, .customer-grid, .summary-grid { grid-template-columns: 1fr; }"
                + "  .header h1 { font-size: 2rem; }"
                + "  .content { padding: 20px; }"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                // Header
                + "<div class='header'>"
                + "<h1>🏨 PARADISE HOTEL</h1>"
                + "<p>Hóa đơn thanh toán - Invoice</p>"
                + "</div>"
                // Content
                + "<div class='content'>"
                // Booking Header
                + "<div class='booking-header'>"
                + "<h2 class='booking-title'>"
                + "<span class='icon'>📋</span>"
                + "Booking #" + bookingCurrent.getId() + " - Checkout Payment"
                + "</h2>"
                + "<div class='booking-details'>"
                + "<div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>📅</span>"
                + "<span class='detail-label'>Check-in:</span>"
                + "<span class='detail-value'>" + dateFormat.format(bookingCurrent.getCheckIn()) + "</span>"
                + "</div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>📅</span>"
                + "<span class='detail-label'>Check-out:</span>"
                + "<span class='detail-value'>" + dateFormat.format(bookingCurrent.getCheckOut()) + "</span>"
                + "</div>"
                + "</div>"
                + "<div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>✅</span>"
                + "<span class='detail-label'>Status:</span>"
                + "<span class='status-badge'>Checked Out - Paid</span>"
                + "</div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>🕐</span>"
                + "<span class='detail-label'>Invoice Date:</span>"
                + "<span class='detail-value'>" + dateFormat.format(new java.util.Date()) + "</span>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>"
                // Customer Information
                + "<div class='customer-section'>"
                + "<h3 class='section-title'>"
                + "<span class='icon'>👤</span>"
                + "Customer Information"
                + "</h3>"
                + "<div class='customer-grid'>"
                + "<div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>👤</span>"
                + "<span class='detail-label'>Name:</span>"
                + "<span class='detail-value'>" + (user.getFullname() != null ? user.getFullname() : user.getUsername()) + "</span>"
                + "</div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>📧</span>"
                + "<span class='detail-label'>Email:</span>"
                + "<span class='detail-value'>" + user.getEmail() + "</span>"
                + "</div>"
                + "</div>"
                + "<div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>📞</span>"
                + "<span class='detail-label'>Phone:</span>"
                + "<span class='detail-value'>" + (user.getPhonenumber() != null ? user.getPhonenumber() : "N/A") + "</span>"
                + "</div>"
                + "<div class='detail-item'>"
                + "<span class='icon'>⭐</span>"
                + "<span class='detail-label'>Membership:</span>"
                + "<span class='membership-badge'>💎 " + rank + "</span>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>";

        // Room Types Section - GIỮ NGUYÊN GIÁ, KHÔNG CHIA
        if (bookingRoomTypes != null && !bookingRoomTypes.isEmpty()) {
            htmlContent += "<div class='room-types-section'>"
                    + "<h3 class='section-title'>"
                    + "<span class='icon'>🏠</span>"
                    + "Room Types"
                    + "</h3>"
                    + "<table class='room-table'>"
                    + "<thead>"
                    + "<tr>"
                    + "<th>🏠 Room Type</th>"
                    + "<th>🛏️ Number of Rooms</th>"
                    + "<th>🌙 Nights</th>"
                    + "<th>💰 Price/Night</th>"
                    + "<th>🧮 Subtotal</th>"
                    + "</tr>"
                    + "</thead>"
                    + "<tbody>";

            for (BookingRoomType brt : bookingRoomTypes) {
                int nights = brt.getNumberOfNights() > 0 ? brt.getNumberOfNights() : 1;
                String pricePerNight = currencyFormat.format(brt.getPricePerRoom());
                String subtotal = currencyFormat.format(brt.getPricePerRoom().doubleValue() * brt.getQuantity() * nights);

                htmlContent += "<tr>"
                        + "<td><strong>" + brt.getRoomTypeName() + "</strong>"
                        + (brt.getRoomTypeDescription() != null && !brt.getRoomTypeDescription().isEmpty()
                        ? "<br><small style='color: #6c757d;'>" + brt.getRoomTypeDescription() + "</small>" : "")
                        + "</td>"
                        + "<td><span class='quantity-badge'>" + brt.getQuantity() + "</span></td>"
                        + "<td><span class='quantity-badge'>" + nights + "</span></td>"
                        + "<td class='price-text'>" + pricePerNight + " VND</td>"
                        + "<td class='subtotal-text'>" + subtotal + " VND</td>"
                        + "</tr>";
            }

            htmlContent += "</tbody></table></div>";
        }

        // Payment Summary - CHỈ TỔNG THANH TOÁN ĐƯỢC CHIA
        htmlContent += "<div class='payment-summary'>"
                + "<h3 class='section-title'>"
                + "<span class='icon'>💰</span>"
                + "Payment Summary"
                + "</h3>"
                + "<div class='total-section'>"
                + "<p style='margin: 0; font-size: 18px; color: #495057;'>💰 Tổng thanh toán</p>"
                + "<div class='total-amount'>" + totalAmountShort + " VNĐ</div>"
                + "<p style='margin: 0; opacity: 0.9; color: #6c757d;'>Đã bao gồm thuế và phí dịch vụ</p>"
                + "<p style='margin: 10px 0 0 0; color: #28a745; font-weight: 600;'>✅ Đã thanh toán thành công</p>"
                + "</div>"
                + "</div>";

        htmlContent += "<div class='divider'></div>"
                // Thank you section
                + "<div class='thank-you'>"
                + "<h3>✨ Cảm ơn quý khách đã sử dụng dịch vụ của Paradise Hotel!</h3>"
                + "<p>Chúng tôi hy vọng quý khách đã có những trải nghiệm tuyệt vời tại khách sạn của chúng tôi.</p>"
                + "<p>Hẹn gặp lại quý khách trong tương lai!</p>"
                + "<br>"
                + "<p style='font-size: 0.9rem; color: #6c757d;'>Nếu có bất kỳ thắc mắc nào về hóa đơn này, vui lòng liên hệ với chúng tôi qua hotline hoặc email.</p>"
                + "</div>"
                + "</div>"
                // Footer
                + "<div class='footer'>"
                + "<h3>🏨 PARADISE HOTEL</h3>"
                + "<p>📍 123 Đường ABC, Quận XYZ, TP.HCM</p>"
                + "<p>📞 Hotline: 1900-xxxx | 📧 Email: info@paradisehotel.com</p>"
                + "<p>🌐 Website: www.paradisehotel.com</p>"
                + "<div style='margin-top: 20px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.3);'>"
                + "<p style='opacity: 0.8;'>© 2024 Paradise Hotel. All rights reserved.</p>"
                + "<p style='opacity: 0.7; font-size: 0.85rem;'>Invoice generated on "
                + dateFormat.format(new java.util.Date()) + "</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // Gửi email
        Transport.send(message);
    }

    public static void sendPointAdjustmentEmail(
            String toEmail,
            String customerName,
            int changedPoints,
            String reason,
            int newTotalPoints
    ) throws Exception {
        final String fromEmail = "hung70919@gmail.com";
        final String password = "swav xtfi qiqe oeqz";

        String subject = "Notification: Your Loyalty Points Have Been Updated!";
        String htmlContent = "<h2>Dear " + customerName + ",</h2>"
                + "<p>Your loyalty points have been <b>"
                + (changedPoints >= 0 ? "increased" : "decreased") + "</b> by <b>"
                + changedPoints + "</b> points.</p>"
                + "<p><b>Reason:</b> " + reason + "</p>"
                + "<p>Your new total points: <b>" + newTotalPoints + "</b></p>"
                + "<br/>"
                + "<p>Thank you for being a valued customer!</p>"
                + "<p><i>This is an automated email, please do not reply.</i></p>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }
}
