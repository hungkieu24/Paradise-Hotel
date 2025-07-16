/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Dal.HotelBranchDAO;
import Dal.UserAccountDAO;
import Model.HotelBranch;
import Model.UserAccount;
import Utility.PasswordUtils;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author hungk
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
@WebServlet(name = "AccountServlet", urlPatterns = {"/admin/account"})
public class AccountServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (checkLogin(user, session, response)) {
            response.sendRedirect("../login.jsp");
            return;
        }
        
        UserAccountDAO accountDAO = new UserAccountDAO();
        String action = request.getParameter("action");
        String keyword = request.getParameter("searchKeyword");
        String statusValue = request.getParameter("statusValue");

        int page = 1; // trang đầu tiên
        int pageSize = 5; // 1 trang có 5 row
        int totalPages = 0;
        int listSize = 0;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<UserAccount> userAccountList = accountDAO.getAllUsersAccountByPage(page, pageSize);

        if (action != null && action.equals("search")) {

            if (keyword != null) {
                keyword = keyword.trim(); // Xóa dấu cách đầu và cuối
                keyword = keyword.replaceAll("\\s+", " ");
            }

            if (keyword.equalsIgnoreCase("all")) {
                List<UserAccount> listAll = accountDAO.getAllUsersAccount();
                listSize = listAll.size();
            } else {
                userAccountList = accountDAO.searchUserAccounts(keyword, page, pageSize);
                listSize = accountDAO.getTotalUserAccountAfterSearching(keyword);
            }
        } else if (action != null && action.equals("filerStatus")) {
            if (statusValue.equals("Deleted")) {
                userAccountList = accountDAO.getDeletedUserAccounts(page, pageSize);
                listSize = accountDAO.getTotalDeletedUserAccounts();
            } else if (statusValue.equalsIgnoreCase("all")) {
                List<UserAccount> listAll = accountDAO.getAllUsersAccount();
                listSize = listAll.size();
            } else {
                userAccountList = accountDAO.getUserAccountsByStatus(statusValue, page, pageSize);
                listSize = accountDAO.getTotalUserAccountByStatus(statusValue);
            }
        } else {
            List<UserAccount> listAll = accountDAO.getAllUsersAccount();
            listSize = listAll.size();
        }

        totalPages = (int) Math.ceil((double) listSize / pageSize);
        List<String> roleList = accountDAO.getAllRoles();
        List<String> statusList = accountDAO.getAllStatuses();

        HotelBranchDAO branchDAO = new HotelBranchDAO();
        List<HotelBranch> hotelBranchList = branchDAO.getAllHotelBranchesSimple();

        request.setAttribute("action", action);
        request.setAttribute("keyword", keyword);
        request.setAttribute("statusValue", statusValue);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("accountListSize", listSize);
        request.setAttribute("statusList", statusList);
        request.setAttribute("roleList", roleList);
        request.setAttribute("hotelBranchList", hotelBranchList);
        request.setAttribute("userAccountList", userAccountList);
        request.getRequestDispatcher("./account.jsp").forward(request, response);
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
        String action = request.getParameter("action");
        if ("downloadTemplate".equals(action)) {
            downloadExcelTemplate(response);
            return;
        }

        if ("uploadExcel".equals(action)) {
            handleExcelUpload(request, response);
            return;
        }

        if ("AddExcelAccount".equals(action)) {
            handleAddExcelAccounts(request, response);
            return;
        }
    }

    private void handleAddExcelAccounts(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String defaultAvatarUrl = "../img/avatar/avatar.jpg";
        UserAccountDAO accountDAO = new UserAccountDAO();
        boolean overallSuccess = true;
        List<UserAccount> userList = (List<UserAccount>) session.getAttribute("userListSession");
        if (userList != null) {
            for (UserAccount userAccount : userList) {
                String hashPassword = PasswordUtils.hashPassword(userAccount.getPassword());
                userAccount.setPassword(hashPassword);
                userAccount.setAvatar_url(defaultAvatarUrl);
                boolean success = accountDAO.insertUser(userAccount);
                if (!success) {
                    overallSuccess = false;
                }
            }
            String msg = overallSuccess
                    ? "Passed accounts added successfully"
                    : "Some account failed to add.";
            setSessionMessage(session, msg, overallSuccess ? "success" : "error");
        }

        response.sendRedirect("./account");
    }

    private void downloadExcelTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("UserAccount Template");

        // Cấu hình tiêu đề
        String[] headers = {
            "Full Name", "Username", "Email", "Password", "Phone Number", "Role", "Branch ID"
        };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // ✅ Thêm dòng dữ liệu mẫu
        Row sampleRow = sheet.createRow(1);
        sampleRow.createCell(0).setCellValue("Nguyễn Văn A");
        sampleRow.createCell(1).setCellValue("nguyenvana");
        sampleRow.createCell(2).setCellValue("a@example.com");
        sampleRow.createCell(3).setCellValue("abcd1234");
        sampleRow.createCell(4).setCellValue("0909123456");
        sampleRow.createCell(5).setCellValue("Manager"); // dropdown sẽ hỗ trợ
        sampleRow.createCell(6).setCellValue("1");       // dropdown sẽ hỗ trợ

        // ✅ Tạo dropdown cho Role (chỉ chọn được Manager hoặc Staff)
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint roleConstraint = dvHelper.createExplicitListConstraint(new String[]{"Manager", "Staff"});
        CellRangeAddressList roleRange = new CellRangeAddressList(1, 100, 5, 5); // Từ dòng 2 đến 101, cột F (5)
        DataValidation roleValidation = dvHelper.createValidation(roleConstraint, roleRange);
        roleValidation.setShowErrorBox(true);
        sheet.addValidationData(roleValidation);

        // ✅ Tạo dropdown cho Branch ID từ list
        HotelBranchDAO branchDAO = new HotelBranchDAO();
        List<HotelBranch> hotelBranchList = branchDAO.getAllHotelBranchesSimple();

        // Chuyển danh sách branch ID thành mảng String
        String[] branchIdList = hotelBranchList.stream()
                .map(b -> String.valueOf(b.getId())) // hoặc b.getBranchID() tùy theo tên getter
                .toArray(String[]::new);

        DataValidationConstraint branchConstraint = dvHelper.createExplicitListConstraint(branchIdList);
        CellRangeAddressList branchRange = new CellRangeAddressList(1, 100, 6, 6); // Cột G (6)
        DataValidation branchValidation = dvHelper.createValidation(branchConstraint, branchRange);
        branchValidation.setShowErrorBox(true);
        sheet.addValidationData(branchValidation);

        // ✅ Cấu hình HTTP để trình duyệt hiểu đây là file Excel tải về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=UserAccountTemplate.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void handleExcelUpload(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        try {
            Part filePart = request.getPart("excelFile");

            Map<String, List<Integer>> errorMap = new LinkedHashMap<>();
            List<UserAccount> userList = parseUserAccountsFromExcel(filePart, errorMap);

            request.setAttribute("userList", userList);
            session.setAttribute("userListSession", userList);
            request.setAttribute("userListSize", userList.size());
            request.setAttribute("errorMap", errorMap);

            request.getRequestDispatcher("/admin/view_uploaded_accounts.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            setSessionMessage(session, "Lỗi khi xử lý file Excel: " + e.getMessage(), "error");
            request.getRequestDispatcher("./account.jsp").forward(request, response);
        }
    }

    private List<UserAccount> parseUserAccountsFromExcel(Part filePart, Map<String, List<Integer>> errorMap) throws IOException {

        List<UserAccount> userList = new ArrayList<>();

        HotelBranchDAO branchDAO = new HotelBranchDAO();
        List<HotelBranch> branchList = branchDAO.getAllHotelBranchesSimple();
        int maxBranchId = branchList.stream().mapToInt(HotelBranch::getId).max().orElse(0);

        Set<String> usernameSet = new HashSet<>();
        Set<String> emailSet = new HashSet<>();
        Set<String> phoneSet = new HashSet<>();

        try (InputStream inputStream = filePart.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String fullName = getCellValue(row.getCell(0));
                String username = getCellValue(row.getCell(1));
                String email = getCellValue(row.getCell(2));
                String password = getCellValue(row.getCell(3));
                String phone = getCellValue(row.getCell(4));
                String role = getCellValue(row.getCell(5));
                String branchIdStr = getCellValue(row.getCell(6));

                if (username.isEmpty() || email.isEmpty() || branchIdStr.isEmpty()) {
                    addError(errorMap, "Missing required information", i);
                    continue;
                }

                // Kiểm tra Branch ID
                int branchId;
                try {
                    branchId = Integer.parseInt(branchIdStr.trim());
                    if (branchId > maxBranchId) {
                        addError(errorMap, "Branch ID does not exist", i);
                        continue;
                    }
                } catch (NumberFormatException e) {
                    addError(errorMap, "Invalid Branch ID", i);
                    continue;
                }

                if (!validateRow(i, role, email, username, password, phone,
                        usernameSet, emailSet, phoneSet, errorMap)) {
                    continue;
                }

                // Nếu mọi thứ hợp lệ → thêm vào danh sách
                userList.add(new UserAccount(
                        username,
                        password,
                        email,
                        null,
                        role,
                        phone,
                        branchId,
                        fullName
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error when processing Excel file: " + e.getMessage());
        }

        return userList;
    }

    private void addError(Map<String, List<Integer>> errorMap, String errorMessage, int rowIndex) {
        errorMap.computeIfAbsent(errorMessage, k -> new ArrayList<>()).add(rowIndex);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING ->
                cell.getStringCellValue().trim();
            case NUMERIC ->
                String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN ->
                String.valueOf(cell.getBooleanCellValue());
            default ->
                "";
        };
    }

    private boolean validateRow(
            int rowIndex,
            String role, String email, String username, String password, String phone,
            Set<String> usernameSet, Set<String> emailSet, Set<String> phoneSet,
            Map<String, List<Integer>> errorMap) {
        boolean isValid = true;
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
        Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

        if (!role.equalsIgnoreCase("Manager") && !role.equalsIgnoreCase("Staff")) {
            addError(errorMap, "Invalid role (Manager or Staff only)", rowIndex);
            isValid = false;
        }

        if (!emailPattern.matcher(email).matches()) {
            addError(errorMap, "Email format is incorrect.", rowIndex);
            isValid = false;
        }

        if (!usernameSet.add(username)) {
            addError(errorMap, "Duplicate username in file", rowIndex);
            isValid = false;
        }

        if (!emailSet.add(email)) {
            addError(errorMap, "Duplicate emails in file", rowIndex);
            isValid = false;
        }

        if (!passwordPattern.matcher(password).matches()) {
            addError(errorMap, "Password must be at least 8 characters, include both letters and numbers", rowIndex);
            isValid = false;
        }

        if (!phone.isEmpty() && !phoneSet.add(phone)) {
            addError(errorMap, "Duplicate phone number in file", rowIndex);
            isValid = false;
        }

        return isValid;
    }


}
