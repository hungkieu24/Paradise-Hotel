/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author hungk
 */
public class EmailUtility {

    public static void sendEmail(String toEmail, String subject, String messageText) throws Exception {
        final String fromEmail = "hungkdhe182234@fpt.edu.vn"; // Thay bằng email của bạn
        final String password = "fpdd kxon kpji bqhr"; // App Password của Gmail

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
                + "<p>Vui lòng không chia sẻ mã này với người khác.</p>"
                + "</body>"
                + "</html>";

        message.setContent(htmlContent, "text/html; charset=UTF-8");

        // Gửi email
        Transport.send(message);

    }
}
