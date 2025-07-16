/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Dal.BackupHistoryDAO;
import Model.BackupHistory;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "BackupHistoryServlet", urlPatterns = {"/admin/backup"})
public class BackupHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (checkLogin(user, session, response)) {
            response.sendRedirect("../login.jsp");
            return;
        }
        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();
        String action = request.getParameter("action");
        String keyword = request.getParameter("searchKeyword");

        int page = 1; // trang đầu tiên
        int pageSize = 5; // 1 trang có 5 row
        int totalPages = 0;
        int listSize = 0;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<BackupHistory> backupHistoryList = backupHistoryDAO.getListBackupHistoryByPage(page, pageSize);

        if (action != null && action.equals("search")) {

            if (keyword != null) {
                keyword = keyword.trim(); // Xóa dấu cách đầu và cuối
                keyword = keyword.replaceAll("\\s+", " ");
            }

            if (keyword.equalsIgnoreCase("all")) {
                List<BackupHistory> listAll = backupHistoryDAO.getAllBackupHistories();
                listSize = listAll.size();
            } else {
                backupHistoryList = backupHistoryDAO.searchBackupHistories(keyword, page, pageSize);
                listSize = backupHistoryDAO.getTotalBackupHistoryAfterSearching(keyword);
            }
        } else if (action != null && action.equals("sortNewest")) {
            backupHistoryList = backupHistoryDAO.getAllBackupHistoriesSortedByNewest();
            List<BackupHistory> listAll = backupHistoryDAO.getAllBackupHistories();
            listSize = listAll.size();
        } else if (action != null && action.equals("sortOldest")) {
            backupHistoryList = backupHistoryDAO.getListBackupHistoryByPage(page, pageSize);
            List<BackupHistory> listAll = backupHistoryDAO.getAllBackupHistories();
            listSize = listAll.size();
        } else {
            List<BackupHistory> listAll = backupHistoryDAO.getAllBackupHistories();
            listSize = listAll.size();
        }

        totalPages = (int) Math.ceil((double) listSize / pageSize);

        request.setAttribute("action", action);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("backupHistoryListSize", listSize);
        request.setAttribute("backupHistoryList", backupHistoryList);
        request.getRequestDispatcher("./backup.jsp").forward(request, response);
    }

    private boolean checkLogin(UserAccount user, HttpSession session, HttpServletResponse response) throws IOException {
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            return true;
        }
        return false;
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        if (action.equals("backup")) {
            String typeBackup = request.getParameter("typeBackup");
            if (typeBackup.equals("Full")) {
                handleFullBackup(request, response, session);
                return;
            }
            if (typeBackup.equals("Differential")) {
                handleDifferentialBackup(request, response, session);
                return;
            }
        }

        if (action.equals("downloadFullBackup")) {
            handleDownloadBackupFile(request, response);
            return;
        }

        if (action.equals("downloadPartialBackup")) {
            handleDownloadBackupFile(request, response);
            return;
        }

        if (action.equals("deleteSoft")) {
            String deleteIDString = request.getParameter("deleteID");
            int deleteID = Integer.parseInt(deleteIDString);
            boolean success = backupHistoryDAO.markRowAsDeleted("BackupHistory", deleteID);
            setSessionMessage(session,
                    success ? "Delete successfully!" : "Fail to delete",
                    success ? "success" : "error");
        }

        if (action.equals("restore")) {
            String restoreIDString = request.getParameter("restoreID");
            int restoreID = Integer.parseInt(restoreIDString);
            boolean success = backupHistoryDAO.markRowAsRestore("BackupHistory", restoreID);
            setSessionMessage(session,
                    success ? "Restore successfully!" : "Fail to restore",
                    success ? "success" : "error");
        }

        response.sendRedirect("./backup");
    }

    private void handleFullBackup(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {

        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();
        String backupFolderPath = "D:\\backup";
        String dbName = "HotelBookingSystemDB";
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = dbName + "_" + timestamp + ".bak";
        String fullPath = backupFolderPath + File.separator + fileName;

        // Bước 1: Tạo thư mục nếu chưa tồn tại
        File folder = new File(backupFolderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                setSessionMessage(session, "Unable to create folder backup: " + backupFolderPath, "error");
                response.sendRedirect("./backup");
                return;
            }
        }
        double estimatedSizeMb = 0.0;
        int logId = backupHistoryDAO.insertBackupHistoryReturnId("FULL", fullPath, estimatedSizeMb);

        if (logId != -1) {
            File backupFile = backupHistoryDAO.backupDatabaseToFile(backupFolderPath, dbName, fullPath);
            if (backupFile != null) {
                double actualSizeMb = backupFile.length() / (1024.0 * 1024.0);

                backupHistoryDAO.updateFileSizeById(logId, actualSizeMb);
                setSessionMessage(session, "Create full backup successfully!", "success");
            } else {
                backupHistoryDAO.deleteBackupHistoryById(logId);
                setSessionMessage(session, "Backup failed: Cannot create .bak file.", "error");
            }
        } else {
            setSessionMessage(session, "Failed to insert log entry. Backup not attempted.", "error");
        }

        response.sendRedirect("./backup");
    }

    private void handleDifferentialBackup(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {

        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();

        if (!backupHistoryDAO.hasFullBackup()) {
            setSessionMessage(session, "No FULL BACKUP found. Please create a FULL BACKUP before running a DIFFERENTIAL BACKUP.", "error");
            response.sendRedirect("./backup");
            return;
        }

        String backupFolderPath = "D:\\backup";
        String dbName = "HotelBookingSystemDB";
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = dbName + "_DIFF_" + timestamp + ".bak";
        String fullPath = backupFolderPath + File.separator + fileName;

        // Bước 1: Tạo thư mục nếu chưa tồn tại
        File folder = new File(backupFolderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                setSessionMessage(session, "Unable to create folder backup: " + backupFolderPath, "error");
                response.sendRedirect("./backup");
                return;
            }
        }

        double estimatedSizeMb = 0.0;
        int logId = backupHistoryDAO.insertBackupHistoryReturnId("DIFFERENTIAL", fullPath, estimatedSizeMb);

        if (logId != -1) {
            File backupFile = backupHistoryDAO.backupDatabaseDifferential(backupFolderPath, dbName, fullPath);
            if (backupFile != null) {
                double actualSizeMb = backupFile.length() / (1024.0 * 1024.0);

                backupHistoryDAO.updateFileSizeById(logId, actualSizeMb);
                setSessionMessage(session, "Create differential backup successfully!", "success");
            } else {
                backupHistoryDAO.deleteBackupHistoryById(logId);
                setSessionMessage(session, "Differential backup failed: Cannot create .bak file.", "error");
            }
        } else {
            setSessionMessage(session, "Failed to insert log entry. Backup not attempted.", "error");
        }

        response.sendRedirect("./backup");
    }

    private void handleDownloadBackupFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullPath = request.getParameter("backupPath");

        if (fullPath == null || fullPath.trim().isEmpty()) {
            setSessionMessage(request.getSession(), "Invalid backup path.", "error");
            return;
        }

        File file = new File(fullPath);
        if (!file.exists()) {
            setSessionMessage(request.getSession(), "Backup file not found: " + fullPath, "error");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void handlePartialBackup1(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws IOException {
        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();
        List<String> tablesToBackup = backupHistoryDAO.getAllTableNames();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String dbName = "HotelBookingSystemDB";
        String outputPath = "D:\\backup\\data_backup" + dbName + "_" + timestamp + ".sql";

        double estimatedSizeMb = 0.0;

        int logId = backupHistoryDAO.insertBackupHistoryReturnId("PARTIAL", outputPath, estimatedSizeMb);

        if (logId != -1) {
            File backupFile = backupHistoryDAO.exportTablesToInsertSQLFile(tablesToBackup, outputPath);
            if (backupFile != null && backupFile.exists()) {

                double actualSizeMb = backupFile.length() / (1024.0 * 1024.0);
                backupHistoryDAO.updateFileSizeById(logId, actualSizeMb);
                setSessionMessage(session, "Create partial backup successfully!", "success");
            } else {
                backupHistoryDAO.deleteBackupHistoryById(logId);
                setSessionMessage(session, "Backup failed: Cannot create partial backup file.", "error");
            }
        } else {
            setSessionMessage(session, "Failed to insert log entry. Backup not attempted.", "error");
        }

        response.sendRedirect("./backup");
    }

    private void handleDownloadFullBackup(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String fullPath = request.getParameter("backupPath"); // Đường dẫn tuyệt đối, ví dụ D:\backup\HotelBookingSystemDB_...

        if (fullPath == null || fullPath.trim().isEmpty()) {
            setSessionMessage(session, "Invalid backup path.", "error");
            return;
        }

        File file = new File(fullPath);
        if (!file.exists()) {
            setSessionMessage(session, "File not found: " + fullPath, "error");
            return;
        }

        // Thiết lập header để trình duyệt tải file
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream in = new FileInputStream(file); OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private void handleDownloadPartialBackup(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String fullPath = request.getParameter("backupPath"); // đường dẫn tuyệt đối file .sql

        if (fullPath == null || fullPath.trim().isEmpty()) {
            response.setContentType("text/plain");
            response.getWriter().write("❌ Invalid backup path.");
            return;
        }

        File file = new File(fullPath);
        if (!file.exists()) {
            response.setContentType("text/plain");
            response.getWriter().write("❌ File not found: " + fullPath);
            return;
        }

        // Thiết lập header để tải file
        response.setContentType("application/sql");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLengthLong(file.length());

        FileInputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

}
