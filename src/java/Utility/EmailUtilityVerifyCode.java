/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author thien
 */
public class EmailUtilityVerifyCode {
    public static void sendEmail(String toEmail, String subject,String messageText) throws MessagingException{
        final String fromEmail = "hotelparadise.work@gmail.com";// email người gửi 
        final String password = "nwyv eife dbhb piok";// App password(không phải mật khẩu email);
        
        // cấu hình SMTP server
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");// máy chủ gửi thư của gg
        props.put("mail.smtp.port", "587");// cổng hỗ trợ giao thức Starttls
        props.put("mail.smtp.auth", "true");// cần xác thực trước khi gửi
        props.put("mail.smtp.starttls.enable", "true");// bật mã hóa bảo mật TLS
        
        //xác nhận tài khoản người gửi
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(fromEmail, password);
            }
        });
        // Tạo nội dung email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        
        // Noi dung Html cua email
        String htmlContent = "<html>" 
                +"<body>"
                +"<p>Chào bạn, </p>"
                +"<p>Mã xác nhận của bạn là: <strong style = 'color blue;'>"+ messageText+"</strong></p>"
                +"<p style='color : red;'>Mã của bạn tồn tại trong 1 phút. Vui lòng bạn không chia sẻ mã này với người khác!</p>"
                + "</body>"
                + "</html>";
        message.setContent(htmlContent, "text/html; charset=UTF-8");
        // gửi email
        Transport.send(message);
    }
}
