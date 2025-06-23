/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Dal.UserAccountDAO;
import Model.UserAccount;
import Utility.EmailUtility;
import Utility.PasswordUtils;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hungk
 */
@WebServlet(name = "AccountEventHandlerServlet", urlPatterns = {"/admin/accountEventHandler"})
public class AccountEventHandlerServlet extends HttpServlet {

    private final UserAccountDAO accountDAO = new UserAccountDAO();

    private static final String COL_Email = "email";
    private static final String COL_USERNAME = "username";
    private static final String COL_STATUS = "status";
    private static final String COL_ROLE = "role";
    private static final String COL_PASSWORD = "password";
    private static final String COL_IS_DELETED = "is_deleted";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if (action.equals("changeRole")) {
            String userId = request.getParameter("userId");
            handleChangeRole(request, response, session, userId);
            return;
        }

        if (action.equals("multyActions")) {
            String userId = request.getParameter("userId");
            handleMultiActions(request, response, session, userId);
            return;
        }

        if (action.equals("bulkActions")) {
            handleBulkActions(request, response, session);
            return;
        }

        if (action.equals("confirmTransfer")) {
            String userId = request.getParameter("userId");
            handleConfirmTransferAjax(request, response, session, userId);
            return;
        }

        if (action.equals("resendCode")) {
            handleResendCodeAjax(response, session);
            return;
        }

        if (action.equals("verifyCode")) {
            handleVerifyCodeAjax(request, response, session);
            return;
        }

        if (action.equals("changeOwnerBySelect")) {
            handleChangeOwnerBySelect(request, response, session);
            return;
        }

        if (action.equals("sendCodeToCreateOwner")) {
            handleSendCodeToCreateOwnerAjax(request, response, session);
            return;
        }

        if (action.equals("resendCodeCreate")) {
            handleResendCodeCreateAjax(request, response, session);
            return;
        }

        if (action.equals("verifyCodeCreate")) {
            handleVerifyCodeCreate(request, response, session);
            return;
        }

        if (action.equals("sendCodeToAdd")) {
            handleSendCodeToAdd(request, response);
            return;
        }

        if (action.equals("resendCodeToAdd")) {
            handleResendCodeToAdd(request, response);
            return;
        }

        if (action.equals("verifyCodeToAdd")) {
            handleVerifyCodeToAdd(request, response, session);
            return;
        }

        response.sendRedirect("./account");
    }

    public boolean isValidRegistration(String email, String username, HttpServletRequest request) {
        HttpSession session = request.getSession();

        // Kiểm tra email và username đã tồn tại chưa
        UserAccountDAO accountDAO = new UserAccountDAO();
        if (accountDAO.isFieldExists(COL_Email, email, null)) {
            setSessionMessage(session, "Email already exists!", "error");
            return false;
        }

        if (accountDAO.isFieldExists(COL_USERNAME, username, null)) {
            setSessionMessage(session, "Username already exists!", "error");
            return false;
        }

        // Nếu qua hết thì hợp lệ
        return true;
    }

    private void handleVerifyCodeToAdd(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String code = request.getParameter("codeInput");
        String expectedCode = (String) session.getAttribute("add_email_code");
        long expiry = (long) session.getAttribute("add_email_expiry");

        if (code == null || expectedCode == null || System.currentTimeMillis() > expiry) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"message\": \"Code is missing or expired.\"}");
            return;
        }

        if (!code.equals(expectedCode)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"message\": \"Incorrect verification code.\"}");
            return;
        }

        // Lấy dữ liệu đã lưu trong session
        String emailAdd = (String) session.getAttribute("add_email");
        String fullNameAdd = (String) session.getAttribute("add_fullName");
        String phoneAdd = (String) session.getAttribute("add_phone");
        String usernameAdd = (String) session.getAttribute("add_username");
        String passwordAdd = (String) session.getAttribute("add_password");
        String roleAdd = (String) session.getAttribute("add_role");
        String brandIDAddString = (String) session.getAttribute("add_branchId");

        UserAccount newAcc = new UserAccount();
        if (!roleAdd.equals("Admin")) {
            int brandIDAdd = Integer.parseInt(brandIDAddString);
            newAcc.setBranchId(brandIDAdd);
        }
        String hashPassword = PasswordUtils.hashPassword(passwordAdd);
        newAcc.setUsername(usernameAdd);
        newAcc.setEmail(emailAdd);
        newAcc.setPassword(hashPassword);
        newAcc.setPhonenumber(phoneAdd);
        newAcc.setAvatar_url("./img/avatar/avatar.jpg");
        newAcc.setFullname(fullNameAdd);
        newAcc.setRole(roleAdd);

        boolean success = accountDAO.insertUser(newAcc);
        if (success) {
            setSessionMessage(session, "Create successfully!", "success");
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Account created successfully.\"}");
        } else {
            setSessionMessage(session, "Failed to create account.", "error");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"message\": \"Failed to insert user.\"}");
        }
    }

    private void handleSendCodeToAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String branchIdStr = request.getParameter("branchId");

        String code = String.format("%06d", new Random().nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + 60 * 1000;
        boolean valid = isValidRegistration(email, username, request);
        if (!valid) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "{\"message\": \"Email or Username already exists!\"}");
            return;
        }
        try {
            EmailUtility.sendEmail(email, "Your verification code for new account", code);

            HttpSession session = request.getSession();
            session.setAttribute("add_email_code", code);
            session.setAttribute("add_email_expiry", expiryTime);
            session.setAttribute("add_email", email);
            session.setAttribute("add_fullName", fullName);
            session.setAttribute("add_phone", phone);
            session.setAttribute("add_username", username);
            session.setAttribute("add_password", password);
            session.setAttribute("add_role", role);
            session.setAttribute("add_branchId", branchIdStr);

            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Verification code sent to email.\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send verification code.");
        }
    }

    private void handleResendCodeToAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("add_email");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email is missing in session.");
            return;
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + 60 * 1000;

        try {
            EmailUtility.sendEmail(email, "Your new verification code", code);

            session.setAttribute("add_email_code", code);
            session.setAttribute("add_email_expiry", expiryTime);

            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Verification code resent successfully.\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resend code.");
        }
    }

    private void handleVerifyCodeCreate(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String inputCode = request.getParameter("inputCode");
        String sessionCode = (String) session.getAttribute("newEmailVerificationCode");
        Long expiryTime = (Long) session.getAttribute("newEmailExpiryTime");

        if (sessionCode == null || expiryTime == null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"Session expired or invalid.\"}");
            return;
        }

        if (System.currentTimeMillis() > expiryTime) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"Verification code has expired.\"}");
            return;
        }

        if (!sessionCode.equals(inputCode)) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"Incorrect or expired verification code.\"}");
            return;
        }

        // Lấy lại dữ liệu tạo tài khoản từ session
        String email = (String) session.getAttribute("newEmail");
        String fullName = (String) session.getAttribute("newFullName");
        String phone = (String) session.getAttribute("newPhone");
        String username = (String) session.getAttribute("newUsername");
        String password = (String) session.getAttribute("newPassword");
        String hashedPassword = PasswordUtils.hashPassword(password);

        // Tạo tài khoản mới
        UserAccount newOwner = new UserAccount();
        newOwner.setEmail(email);
        newOwner.setFullname(fullName);
        newOwner.setPhonenumber(phone);
        newOwner.setUsername(username);
        newOwner.setPassword(hashedPassword);
        newOwner.setAvatar_url("./img/avatar/avatar.jpg");
        newOwner.setRole("HotelOwner");

        boolean created = accountDAO.insertHotelOwner(newOwner);

        if (created) {
            // Xóa owner cũ (đặt isDeleted = true)
            String ownerId = (String) session.getAttribute("ownerId");
            if (ownerId != null) {
                accountDAO.updateUserField(COL_IS_DELETED, true, ownerId);
            }

            session.removeAttribute("newEmailVerificationCode");
            session.removeAttribute("newEmail");
            session.removeAttribute("newEmailExpiryTime");
            session.removeAttribute("newFullName");
            session.removeAttribute("newPhone");
            session.removeAttribute("newUsername");
            session.removeAttribute("newPassword");

            setSessionMessage(session, "Transfer of ownership successful!", "success");
        } else {
            setSessionMessage(session, "Failed to delete old owner account", "error");
        }
        response.sendRedirect("./account");
    }

    private void handleChangeOwnerBySelect(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String userId = request.getParameter("userId");

        UserAccount oldOwner = accountDAO.getHotelOwner();
        boolean deleted = accountDAO.updateUserField(COL_IS_DELETED, true, oldOwner.getId());

        if (deleted) {
            boolean success = accountDAO.updateUserField(COL_ROLE, "HotelOwner", userId);
            setSessionMessage(session,
                    success ? "Transfer of ownership successful!" : "Failed to transfer",
                    success ? "success" : "error");
        } else {
            setSessionMessage(session, "Failed to delete old owner account", "error");
        }

        response.sendRedirect("./account");
    }

    private void handleSendCodeToCreateOwnerAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String code = String.format("%06d", new Random().nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + 60 * 1000;

        try {
            EmailUtility.sendEmail(email, "Verify new hotel owner email", code);

            session.setAttribute("newEmailVerificationCode", code);
            session.setAttribute("newEmail", email);
            session.setAttribute("newEmailExpiryTime", expiryTime);
            session.setAttribute("newFullName", fullName);
            session.setAttribute("newPhone", phone);
            session.setAttribute("newUsername", username);
            session.setAttribute("newPassword", password);

            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Verification code sent to new email.\"}");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send email");
        }
    }

    private void handleResendCodeCreateAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String email = (String) session.getAttribute("newEmail");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No email in session.");
            return;
        }

        String code = String.format("%06d", new Random().nextInt(1000000));
        long expiryTime = System.currentTimeMillis() + 60 * 1000;

        try {
            EmailUtility.sendEmail(email, "Resend verification code for new owner", code);

            session.setAttribute("newEmailVerificationCode", code);
            session.setAttribute("newEmailExpiryTime", expiryTime);

            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Verification code resent.\"}");
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resend code.");
        }
    }

    private void handleConfirmTransferAjax(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String userId) throws IOException {
        String userEmail = request.getParameter("userEmail");
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        int duration = 60;
        long expiryTime = System.currentTimeMillis() + duration * 1000;

        try {
            EmailUtility.sendEmail(userEmail, "Code confirm to transfer ownership", verificationCode);
            session.setAttribute("expiryTime", expiryTime);
            session.setAttribute("authCode", verificationCode);
            session.setAttribute("userEmail", userEmail);
            session.setAttribute("ownerId", userId);

            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"success\", \"message\":\"Send email successfully\"}");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to send email");
        }
    }

    private void handleResendCodeAjax(HttpServletResponse response, HttpSession session) throws IOException {
        String userEmail = (String) session.getAttribute("userEmail");
        String userId = (String) session.getAttribute("ownerId");

        if (userEmail != null && userId != null) {
            String newCode = String.format("%06d", new Random().nextInt(1000000));
            long expiryTime = System.currentTimeMillis() + 60 * 1000;

            try {
                EmailUtility.sendEmail(userEmail, "Resend verification code", newCode);
                session.setAttribute("authCode", newCode);
                session.setAttribute("expiryTime", expiryTime);

                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Verification code has been resent.\"}");
            } catch (Exception ex) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resend code.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Session expired.");
        }
    }

    private void handleVerifyCodeAjax(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String inputCode = request.getParameter("inputCode");
        String actualCode = (String) session.getAttribute("authCode");

        Object expiryObj = session.getAttribute("expiryTime");
        if (expiryObj == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No verification process started.");
            return;
        }

        long expiryTime = (long) expiryObj;
        if (System.currentTimeMillis() > expiryTime) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Code expired.");
            return;
        }

        if (actualCode != null && actualCode.equals(inputCode)) {
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Verification successful.\"}");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect verification code.");
        }
    }

    private void handleChangeRole(HttpServletRequest request, HttpServletResponse response, HttpSession session, String userId)
            throws IOException {
        String roleName = request.getParameter("roleName");

        // Lấy tài khoản
        UserAccount account = accountDAO.getUserAccountById(userId);

        // Cập nhật role
        boolean isUpdated = accountDAO.updateUserField(COL_ROLE, roleName, userId);

        // Thông báo kết quả
        String message = isUpdated
                ? "Change role for " + account.getFullname() + " successfully!"
                : "Failure to change role!";
        setSessionMessage(session, message, isUpdated ? "success" : "error");

        // Chuyển hướng về trang account
        response.sendRedirect("./account");
    }

    private void handleMultiActions(HttpServletRequest request, HttpServletResponse response, HttpSession session, String userId)
            throws IOException {
        String actionType = request.getParameter("actionType");
        UserAccount account = accountDAO.getUserAccountById(userId);
        boolean success = false;

        try {
            if (actionType.contains("Active")) {
                success = accountDAO.updateUserField(COL_STATUS, "Active", userId);

            } else if (actionType.contains("Inactivate")) {
                success = accountDAO.updateUserField(COL_STATUS, "Inactive", userId);

            } else if (actionType.contains("Ban")) {
                boolean isAdminOrHotelOwner = accountDAO.isAdminOrHotelOwner(userId);
                if (isAdminOrHotelOwner) {
                    boolean lastAmin = accountDAO.isLastActiveAccountOfRole("Admin");
                    if (lastAmin) {
                        success = false;
                    }
                } else {
                    success = accountDAO.updateUserField(COL_STATUS, "Banned", userId);
                }

            } else if (actionType.contains("Unban")) {
                success = accountDAO.updateUserField(COL_STATUS, "Active", userId);

            } else if (actionType.contains("Delete")) {
                boolean isAdminOrHotelOwner = accountDAO.isAdminOrHotelOwner(userId);
                if (isAdminOrHotelOwner) {
                    boolean lastAmin = accountDAO.isLastActiveAccountOfRole("Admin");
                    if (lastAmin) {
                        success = false;
                    }
                } else {
                    success = accountDAO.updateUserField(COL_IS_DELETED, true, userId);
                }

            } else if (actionType.contains("Restore")) {
                success = accountDAO.updateUserField(COL_IS_DELETED, false, userId);

            } else if (actionType.contains("Reset")) {
                handleResetPassword(account, userId, session);
                response.sendRedirect("./account");
                return;
            }

            String message = success
                    ? "Change status for " + account.getFullname() + " successfully!"
                    : "Failure to change status!";
            setSessionMessage(session, message, success ? "success" : "error");
            response.sendRedirect("./account");

        } catch (Exception ex) {
            ex.printStackTrace();
            setSessionMessage(session, "Error while processing action", "error");
            response.sendRedirect("./account");
        }
    }

    private boolean handleResetPassword(UserAccount account, String userId, HttpSession session)
            throws IOException {
        try {
            String tempoPassword = generateTemporaryPassword(8);
            EmailUtility.sendResetPasswordEmail(account.getEmail(), "Reset password", tempoPassword);
            String hashedPassword = PasswordUtils.hashPassword(tempoPassword);

            boolean success = accountDAO.updateUserField(COL_PASSWORD, hashedPassword, userId);
            setSessionMessage(session,
                    success ? "Send Email Successfully!" : "Failed to reset password",
                    success ? "success" : "error");
            return success;
        } catch (Exception ex) {
            ex.printStackTrace();
            setSessionMessage(session, "Unable to send email, please check email", "error");
            return false;
        }
    }

    // Hàm tạo mật khẩu ngẫu nhiên
    private String generateTemporaryPassword(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    private void handleBulkActions(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        String actionType = request.getParameter("bulkActionType");
        String listUserIdRaw = request.getParameter("listUserId");

        if (listUserIdRaw == null || listUserIdRaw.trim().isEmpty()) {
            setSessionMessage(session, "No users selected!", "error");
            response.sendRedirect("./account");
            return;
        }

        String[] userIds = listUserIdRaw.split(",\\s*");
        boolean overallSuccess = true;

        try {
            if (actionType.contains("Reset")) {
                for (String userId : userIds) {
                    UserAccount acc = accountDAO.getUserAccountById(userId);
                    boolean resetSuccess = handleResetPassword(acc, userId, session);
                    if (!resetSuccess) {
                        overallSuccess = false;
                    }
                }
                String msg = overallSuccess
                        ? "Reset password emails sent successfully!"
                        : "Some reset emails failed to send.";
                setSessionMessage(session, msg, overallSuccess ? "success" : "error");
                response.sendRedirect("./account");
                return;
            }

            for (String userId : userIds) {
                boolean success = false;

                if (actionType.contains("Active")) {
                    success = accountDAO.updateUserField(COL_STATUS, "Active", userId);
                } else if (actionType.contains("Inactive")) {
                    success = accountDAO.updateUserField(COL_STATUS, "Inactive", userId);
                } else if (actionType.contains("Ban")) {
                    boolean isAdminOrHotelOwner = accountDAO.isAdminOrHotelOwner(userId);
                    if (isAdminOrHotelOwner) {
                        boolean lastAmin = accountDAO.isLastActiveAccountOfRole("Admin");
                        if (lastAmin) {
                            success = false;
                        }
                    } else {
                        success = accountDAO.updateUserField(COL_STATUS, "Banned", userId);
                    }
                } else if (actionType.contains("UnBan")) {
                    success = accountDAO.updateUserField(COL_STATUS, "Active", userId);
                } else if (actionType.contains("Delete")) {
                    boolean isAdminOrHotelOwner = accountDAO.isAdminOrHotelOwner(userId);
                    if (isAdminOrHotelOwner) {
                        boolean lastAmin = accountDAO.isLastActiveAccountOfRole("Admin");
                        if (lastAmin) {
                            success = false;
                        }
                    } else {
                        success = accountDAO.updateUserField(COL_IS_DELETED, true, userId);
                    }
                } else if (actionType.contains("Restore")) {
                    success = accountDAO.updateUserField(COL_IS_DELETED, false, userId);
                }

                if (!success) {
                    overallSuccess = false;
                }
            }

            String msg = overallSuccess
                    ? "Bulk action \"" + actionType + "\" applied successfully!"
                    : "Some users could not be updated.";
            setSessionMessage(session, msg, overallSuccess ? "success" : "error");
            response.sendRedirect("./account");

        } catch (Exception ex) {
            ex.printStackTrace();
            setSessionMessage(session, "Error while processing bulk action!", "error");
            response.sendRedirect("./account");
        }
    }

}
