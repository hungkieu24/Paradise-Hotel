/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.BranchMonthlyReportDAO;
import Dal.HotelBranchDAO;
import Dal.InitialInvestmentDAO;
import Model.BranchMonthlyReport;
import Model.HotelBranch;
import Model.InitialInvestment;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
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
@WebServlet(name = "uploadReportServlet", urlPatterns = {"/hotelOwner/uploadReports"})
public class uploadReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        if ("downloadTemplate".equals(action)) {
            downloadReportTemplate(response);
            return;
        }

        if ("uploadTemplate".equals(action)) {
            handleReportUpload(request, response);
            return;
        }

        if ("savePreviewReport".equals(action)) {
            saveReportAndInvestment(session, response);
            return;
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    private void downloadReportTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Branch Report Template");

        // 🔹 Định dạng số (dấu phẩy hàng nghìn)
        DataFormat format = workbook.createDataFormat();
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(format.getFormat("#,##0"));

        // 🔹 Ngày đầu tháng và ngày hiện tại
        LocalDate today = LocalDate.now();
        String reportMonthStr = today.withDayOfMonth(1).toString(); // yyyy-MM-01
        String investDateStr = today.toString();                    // yyyy-MM-dd

        // 🔹 Thông tin chung (Object để giữ kiểu số)
        Object[][] generalInfo = {
            {"Branch ID", "2"},
            {"Branch Name", "Sunshine Hotel Da Nang"},
            {"Report Month", reportMonthStr},
            {"Initial Investment", 0}, // Số, không phải chuỗi!
            {"Invest Date", investDateStr}
        };

        for (int i = 0; i < generalInfo.length; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(generalInfo[i][0].toString());

            Cell valueCell = row.createCell(1);

            Object value = generalInfo[i][1];
            String key = generalInfo[i][0].toString();

            if (value instanceof Number) {
                valueCell.setCellValue(((Number) value).doubleValue());
                valueCell.setCellStyle(numberStyle);
            } else {
                valueCell.setCellValue(value.toString());
            }

            // Thêm comment cho Invest Date
            if ("Invest Date".equals(key)) {
                CreationHelper factory = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = factory.createClientAnchor();
                anchor.setCol1(valueCell.getColumnIndex());
                anchor.setCol2(valueCell.getColumnIndex() + 2);
                anchor.setRow1(row.getRowNum());
                anchor.setRow2(row.getRowNum() + 3);

                Comment comment = drawing.createCellComment(anchor);
                comment.setString(factory.createRichTextString(
                        "Note: Invest Date should be in the SAME month as Report Month."
                ));
                valueCell.setCellComment(comment);
            }
        }

        // 🔹 Header bảng dữ liệu
        int startRow = generalInfo.length + 2;
        Row header = sheet.createRow(startRow);
        header.createCell(0).setCellValue("Type");
        header.createCell(1).setCellValue("Description");
        header.createCell(2).setCellValue("Amount");

        // 🔹 Dữ liệu mẫu
        Object[][] sampleData = {
            {"Revenue", "Dịch vụ massage", 20000000.0},
            {"Revenue", "Đặt phòng", 15000000.0},
            {"Expenses", "Sửa chữa", 3000000.0},
            {"Expenses", "Lương", 40000000.0}
        };

        for (int i = 0; i < sampleData.length; i++) {
            Row row = sheet.createRow(startRow + 1 + i);
            row.createCell(0).setCellValue(sampleData[i][0].toString());
            row.createCell(1).setCellValue(sampleData[i][1].toString());

            Cell amountCell = row.createCell(2);
            amountCell.setCellValue((double) sampleData[i][2]);
            amountCell.setCellStyle(numberStyle); // format số tiền
        }

        // 🔹 Dropdown cho "Type"
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint typeConstraint = dvHelper.createExplicitListConstraint(new String[]{"Revenue", "Expenses"});
        CellRangeAddressList typeRange = new CellRangeAddressList(startRow + 1, startRow + 100, 0, 0);
        DataValidation typeValidation = dvHelper.createValidation(typeConstraint, typeRange);
        typeValidation.setShowErrorBox(true);
        sheet.addValidationData(typeValidation);

        // 🔹 Auto resize columns
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        // 🔹 Trả file về client
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BranchReportTemplate.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private void handleReportUpload(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Part filePart = request.getPart("file");

        if (filePart == null || filePart.getSize() == 0) {
            setSessionMessage(session, "No file selected!", "error");
            response.sendRedirect("./uploadReports.jsp");
            return;
        }

        try (InputStream input = filePart.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(input);
            Sheet sheet = workbook.getSheetAt(0);

            Map<String, String> infoMap = extractGeneralInfo(sheet);
            validateInfoMap(infoMap);

            int branchId = (int) Double.parseDouble(infoMap.get("Branch ID"));
            String branchName = infoMap.getOrDefault("Branch Name", "N/A");
            double newCapital = Double.parseDouble(infoMap.get("Initial Investment"));
            Date reportMonth = Date.valueOf(infoMap.get("Report Month"));
            Date investDate = Date.valueOf(infoMap.get("Invest Date"));

            InitialInvestment investment = new InitialInvestment(branchId, newCapital, investDate);

            if (!validateBranchReportInput(branchId, branchName, reportMonth, investment, session, response)) {
                response.sendRedirect("./uploadReports.jsp");
                return;
            }

            InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
            double previousCapital = investmentDAO.getTotalCapitalByBranch(branchId);
            double totalCapital = previousCapital + newCapital;

            int dataStartRow = findDataStartRow(sheet);
            if (dataStartRow == -1) {
                throw new IllegalStateException("Missing header: Type | Description | Amount");
            }

            ReportData reportData = parseReportData(sheet, dataStartRow);

            BranchMonthlyReport previewReport = buildPreviewReport(branchId, branchName, reportMonth, totalCapital, reportData);

            // Gửi dữ liệu lên session
            session.setAttribute("newCapital", newCapital);
            session.setAttribute("previousCapital", previousCapital);
            session.setAttribute("totalCapital", totalCapital);
            session.setAttribute("previewReport", previewReport);
            session.setAttribute("investment", investment);
            session.setAttribute("revenueList", reportData.revenueList());
            session.setAttribute("expenseList", reportData.expenseList());

        } catch (Exception e) {
            e.printStackTrace();
            setSessionMessage(session, "Error when processing Excel file: " + e.getMessage(), "error");
        }

        response.sendRedirect("./uploadReports.jsp");
    }

    private void saveReportAndInvestment(HttpSession session, HttpServletResponse response) throws IOException {
        BranchMonthlyReport report = (BranchMonthlyReport) session.getAttribute("previewReport");
        InitialInvestment investment = (InitialInvestment) session.getAttribute("investment");

        // 🔹 Lưu báo cáo lợi nhuận
        BranchMonthlyReportDAO reportDAO = new BranchMonthlyReportDAO();
        boolean reportInserted = reportDAO.insertBranchMonthlyReport(report);

        if (!reportInserted) {
            setSessionMessage(session, "Failed to insert BranchMonthlyReport", "error");
            response.sendRedirect("./uploadReports.jsp");
            return;
        }

        // 🔹 Nếu có đầu tư (capital > 0) thì mới lưu investment
        if (investment.getCapital() > 0) {
            InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
            boolean investmentInserted = investmentDAO.insertInitialInvestment(investment);

            if (!investmentInserted) {
                setSessionMessage(session, "Failed to insert InitialInvestment", "error");
                response.sendRedirect("./uploadReports.jsp");
                return;
            }
        }
        // 🔹 Sau khi lưu xong, clear session attributes để tránh lưu lại dữ liệu cũ
        session.removeAttribute("newCapital");
        session.removeAttribute("previousCapital");
        session.removeAttribute("totalCapital");
        session.removeAttribute("previewReport");
        session.removeAttribute("investment");
        session.removeAttribute("revenueList");
        session.removeAttribute("expenseList");

        setSessionMessage(session, "Saved successfully!", "success");
        response.sendRedirect("./uploadReports.jsp");
    }

    private boolean validateBranchReportInput(int branchId, String branchName, Date reportMonth,
            InitialInvestment investment, HttpSession session, HttpServletResponse response) throws IOException {

        HotelBranchDAO branchDAO = new HotelBranchDAO();
        HotelBranch branchDB = branchDAO.getHotelBranchById(branchId);
        if (branchDB == null) {
            setSessionMessage(session, "Branch ID does not exist.", "error");
            return false;
        }

        if (!branchDB.getName().equals(branchName)) {
            setSessionMessage(session, "Branch name does not match.", "error");
            return false;
        }

        BranchMonthlyReportDAO monthlyReportDAO = new BranchMonthlyReportDAO();
        BranchMonthlyReport monthlyReportDB = monthlyReportDAO.getLatestBranchMonthlyReportByBranchId(branchId);
        if (monthlyReportDB != null && monthlyReportDB.getReportMonth().after(reportMonth)) {
            setSessionMessage(session, "Report Month must not be earlier than previous data.", "error");
            return false;
        }

        InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
        InitialInvestment investmentDB = investmentDAO.getLatestInitialInvestmentByBranchId(branchId);
        if (investmentDB != null && investmentDB.getInvestedDate().after(investment.getInvestedDate())) {
            setSessionMessage(session, "Investment date must not be earlier than previous data.", "error");
            return false;
        }

        // 🔹 Kiểm tra ReportMonth và Investment Date phải cùng tháng và năm
        LocalDate reportMonthLocal = reportMonth.toLocalDate();
        LocalDate investDateLocal = investment.getInvestedDate().toLocalDate();

        if (reportMonthLocal.getYear() != investDateLocal.getYear()
                || reportMonthLocal.getMonthValue() != investDateLocal.getMonthValue()) {
            setSessionMessage(session, "Report Month and Investment Date must be in the same month.", "error");
            return false;
        }

        return true;
    }

    private void validateInfoMap(Map<String, String> infoMap) {
        if (!infoMap.containsKey("Report Month") || !infoMap.containsKey("Invest Date")) {
            throw new IllegalArgumentException("Missing report month or invest date");
        }
    }

    private int findDataStartRow(Sheet sheet) {
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            String col0 = getCellValue(row.getCell(0)).toLowerCase();
            String col1 = getCellValue(row.getCell(1)).toLowerCase();
            String col2 = getCellValue(row.getCell(2)).toLowerCase();

            if (col0.equals("type") && col1.equals("description") && col2.equals("amount")) {
                return i + 1;
            }
        }
        return -1;
    }

    private record ReportData(
            List<Map<String, Object>> revenueList,
            List<Map<String, Object>> expenseList,
            double totalRevenue,
            double totalExpenses) {

    }

    private ReportData parseReportData(Sheet sheet, int startRow) {
        List<Map<String, Object>> revenues = new ArrayList<>();
        List<Map<String, Object>> expenses = new ArrayList<>();
        double totalRevenue = 0, totalExpenses = 0;

        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                break;
            }

            String type = getCellValue(row.getCell(0));
            String source = getCellValue(row.getCell(1));
            String amountStr = getCellValue(row.getCell(2));

            if (type.isEmpty() || amountStr.isEmpty()) {
                continue;
            }

            try {
                double amount = Double.parseDouble(amountStr.replace(",", "").trim());
                Map<String, Object> entry = Map.of("source", source, "amount", amount);

                if ("Revenue".equalsIgnoreCase(type)) {
                    revenues.add(entry);
                    totalRevenue += amount;
                } else if ("Expenses".equalsIgnoreCase(type)) {
                    expenses.add(entry);
                    totalExpenses += amount;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount at row " + (i + 1) + ": " + amountStr);
            }
        }

        return new ReportData(revenues, expenses, totalRevenue, totalExpenses);
    }

    private BranchMonthlyReport buildPreviewReport(int branchId, String branchName, Date reportMonth, double Capital, ReportData data) {
        double profit = data.totalRevenue() - data.totalExpenses();
        double profitRate = (Capital > 0) ? (profit / Capital) * 100 : 0;

        BranchMonthlyReport report = new BranchMonthlyReport();
        report.setBranchId(branchId);
        report.setReportMonth(reportMonth);
        report.setRevenue(data.totalRevenue());
        report.setExpenses(data.totalExpenses());
        report.setProfit(profit);
        report.setProfitRate(profitRate);

        HotelBranch branch = new HotelBranch();
        branch.setId(branchId);
        branch.setName(branchName);
        report.setHotelBranch(branch);

        return report;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING ->
                cell.getStringCellValue().trim();
            case NUMERIC ->
                DateUtil.isCellDateFormatted(cell)
                ? new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue())
                : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN ->
                String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    yield cell.getStringCellValue().trim();
                }
            }
            default ->
                cell.toString().trim();
        };
    }

    private Map<String, String> extractGeneralInfo(Sheet sheet) {
        Map<String, String> infoMap = new HashMap<>();

        for (int i = 0; i <= 10; i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                continue;
            }

            String key = getCellValue(row.getCell(0));
            String value = getCellValue(row.getCell(1));

            if (!key.isEmpty()) {
                infoMap.put(key, value);
            }
        }

        return infoMap;
    }

}
