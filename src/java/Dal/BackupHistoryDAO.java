/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dal;

import Model.BackupHistory;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hungk
 */
public class BackupHistoryDAO extends DBcontext.DBContext {

    public List<BackupHistory> getAllBackupHistories() {
        List<BackupHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM BackupHistory";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Định dạng ngày giờ theo kiểu yyyy-MM-dd HH:mm:ss
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("backup_time");
                String formattedTime = sdf.format(timestamp);

                BackupHistory history = new BackupHistory(
                        rs.getInt("id"),
                        formattedTime,
                        rs.getString("backup_type"),
                        rs.getString("backup_path"),
                        rs.getDouble("file_size_mb"),
                        rs.getBoolean("is_deleted")
                );

                histories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return histories;
    }

    public List<BackupHistory> getListBackupHistoryByPage(int page, int pageSize) {
        List<BackupHistory> historyList = new ArrayList<>();
        String sql = "SELECT * FROM BackupHistory ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (page - 1) * pageSize);
            stmt.setInt(2, pageSize);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String formattedTime = sdf.format(rs.getTimestamp("backup_time"));
                    BackupHistory history = new BackupHistory(
                            rs.getInt("id"),
                            formattedTime,
                            rs.getString("backup_type"),
                            rs.getString("backup_path"),
                            rs.getDouble("file_size_mb"),
                            rs.getBoolean("is_deleted")
                    );
                    historyList.add(history);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return historyList;
    }

    public int getTotalBackupHistoryAfterSearching(String keyword) {
        String sql = "SELECT COUNT(*) FROM BackupHistory "
                + "WHERE backup_type LIKE ? "
                + "OR CAST(file_size_mb AS VARCHAR) LIKE ? "
                + "OR FORMAT(backup_time, 'yyyy-MM-dd HH:mm:ss') LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String wildcardKeyword = "%" + keyword + "%";
            stmt.setString(1, wildcardKeyword);
            stmt.setString(2, wildcardKeyword);
            stmt.setString(3, wildcardKeyword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public List<BackupHistory> searchBackupHistories(String keyword, int page, int pageSize) {
        List<BackupHistory> historyList = new ArrayList<>();
        String sql = "SELECT * FROM BackupHistory "
                + "WHERE backup_type LIKE ? "
                + "OR CAST(file_size_mb AS VARCHAR) LIKE ? "
                + "OR FORMAT(backup_time, 'yyyy-MM-dd HH:mm:ss') LIKE ? "
                + "ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String wildcardKeyword = "%" + keyword + "%";
            stmt.setString(1, wildcardKeyword);
            stmt.setString(2, wildcardKeyword);
            stmt.setString(3, wildcardKeyword);
            stmt.setInt(4, (page - 1) * pageSize);
            stmt.setInt(5, pageSize);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String formattedTime = sdf.format(rs.getTimestamp("backup_time"));
                    BackupHistory history = new BackupHistory(
                            rs.getInt("id"),
                            formattedTime,
                            rs.getString("backup_type"),
                            rs.getString("backup_path"),
                            rs.getDouble("file_size_mb"),
                            rs.getBoolean("is_deleted")
                    );
                    historyList.add(history);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyList;
    }

    public List<BackupHistory> getAllBackupHistoriesSortedByNewest() {
        List<BackupHistory> histories = new ArrayList<>();
        String sql = "SELECT * FROM BackupHistory ORDER BY backup_time DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String formattedTime = sdf.format(rs.getTimestamp("backup_time"));
                BackupHistory history = new BackupHistory(
                        rs.getInt("id"),
                        formattedTime,
                        rs.getString("backup_type"),
                        rs.getString("backup_path"),
                        rs.getDouble("file_size_mb"),
                        rs.getBoolean("is_deleted")
                );
                histories.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return histories;
    }

    public boolean insertBackupHistory(String backupType, String backupPath, double fileSizeMb) {
        String sql = "INSERT INTO BackupHistory (backup_type, backup_path, file_size_mb) "
                + "VALUES (?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, backupType);
            ps.setString(2, backupPath);
            ps.setDouble(3, fileSizeMb);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public File backupDatabaseToFile(String backupFolderPath, String dbName, String fullPath) {
        // Bước 2: Backup database
        String backupSql = "BACKUP DATABASE " + dbName + " TO DISK = ?";
        try (PreparedStatement ps = connection.prepareStatement(backupSql)) {
            ps.setString(1, fullPath);
            ps.executeUpdate();

            // Bước 3: Kiểm tra file backup đã tồn tại chưa
            File backupFile = new File(fullPath);
            if (backupFile.exists()) {
                return backupFile;
            } else {
                System.err.println("❌ File backup không được tạo.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public File backupDatabaseDifferential(String backupFolderPath, String dbName, String fullPath) {
        // Backup Differential
        String backupSql = "BACKUP DATABASE " + dbName + " TO DISK = ? WITH DIFFERENTIAL";

        try (PreparedStatement ps = connection.prepareStatement(backupSql)) {
            ps.setString(1, fullPath);
            ps.executeUpdate();

            // Kiểm tra file có tồn tại không
            File backupFile = new File(fullPath);
            if (backupFile.exists()) {
                return backupFile;
            } else {
                System.err.println("❌ File differential backup không được tạo.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasFullBackup() {
        String sql = "SELECT COUNT(*) FROM BackupHistory WHERE backup_type = 'FULL' AND is_deleted = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Trả về true nếu đã có FULL backup
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Nếu có lỗi hoặc không có FULL backup
    }

    public File exportTablesToInsertSQLFile(List<String> tableNames, String outputFilePath) throws IOException {
        File file = new File(outputFilePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String tableName : tableNames) {
                String insertSQL = exportTableToInsertSQL(tableName);
                writer.write("-- Data for table: " + tableName + "\n");
                writer.write(insertSQL);
                writer.write("\n\n");
            }
            writer.flush();
            return file;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String exportTableToInsertSQL(String tableName) throws SQLException {
        StringBuilder builder = new StringBuilder();

        String query = "SELECT * FROM " + tableName;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // Danh sách các cột KHÔNG PHẢI "id"
        List<Integer> validIndexes = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String colName = meta.getColumnName(i);
            if (!colName.equalsIgnoreCase("id")) {
                validIndexes.add(i);
            }
        }

        while (rs.next()) {
            builder.append("INSERT INTO ").append(tableName).append(" (");

            // Thêm tên cột
            for (int j = 0; j < validIndexes.size(); j++) {
                int colIndex = validIndexes.get(j);
                builder.append(meta.getColumnName(colIndex));
                if (j < validIndexes.size() - 1) {
                    builder.append(", ");
                }
            }

            builder.append(") VALUES (");

            // Thêm dữ liệu
            for (int j = 0; j < validIndexes.size(); j++) {
                int colIndex = validIndexes.get(j);
                Object value = rs.getObject(colIndex);

                if (value == null) {
                    builder.append("NULL");
                } else if (value instanceof String || value instanceof Date || value instanceof Timestamp) {
                    builder.append("'").append(value.toString().replace("'", "''")).append("'");
                } else if (value instanceof Boolean) {
                    builder.append((Boolean) value ? 1 : 0); // ✅ boolean to 1/0
                } else {
                    builder.append(value);
                }

                if (j < validIndexes.size() - 1) {
                    builder.append(", ");
                }
            }

            builder.append(");\n");
        }

        return builder.toString();
    }

    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");

                // Bỏ qua bảng bắt đầu bằng 'trace_' hoặc trong schema hệ thống
                if (!tableName.startsWith("trace_")) {
                    tableNames.add(tableName);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableNames;
    }

    public int insertBackupHistoryReturnId(String backupType, String backupPath, double fileSizeMb) {
        String sql = "INSERT INTO BackupHistory (backup_type, backup_path, file_size_mb) OUTPUT INSERTED.id VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, backupType);
            ps.setString(2, backupPath);
            ps.setDouble(3, fileSizeMb);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deleteBackupHistoryById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM BackupHistory WHERE id = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFileSizeById(int id, double fileSizeMb) {
        String sql = "UPDATE BackupHistory SET file_size_mb = ? WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDouble(1, fileSizeMb);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markRowAsDeleted(String tableName, int id) {
        String sql = "UPDATE " + tableName + " SET is_deleted = 1 WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markRowAsRestore(String tableName, int id) {
        String sql = "UPDATE " + tableName + " SET is_deleted = 0 WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        BackupHistoryDAO backupHistoryDAO = new BackupHistoryDAO();
        List<String> tableNames = backupHistoryDAO.getAllTableNames();
        for (String tableName : tableNames) {
            System.out.println(tableName);
        }
    }
}
