/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author hungk
 */
public class BackupHistory {
    private int id;
    private String backup_time;
    private String backup_type;
    private String backup_path;
    private double file_size_mb;
    private boolean is_deleted;

    public BackupHistory() {
    }

    public BackupHistory(int id, String backup_time, String backup_type, String backup_path, double file_size_mb, boolean is_deleted) {
        this.id = id;
        this.backup_time = backup_time;
        this.backup_type = backup_type;
        this.backup_path = backup_path;
        this.file_size_mb = file_size_mb;
        this.is_deleted = is_deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackup_time() {
        return backup_time;
    }

    public void setBackup_time(String backup_time) {
        this.backup_time = backup_time;
    }

    public String getBackup_type() {
        return backup_type;
    }

    public void setBackup_type(String backup_type) {
        this.backup_type = backup_type;
    }

    public String getBackup_path() {
        return backup_path;
    }

    public void setBackup_path(String backup_path) {
        this.backup_path = backup_path;
    }

    public double getFile_size_mb() {
        return file_size_mb;
    }

    public void setFile_size_mb(double file_size_mb) {
        this.file_size_mb = file_size_mb;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return "BackupHistory{" + "id=" + id + ", backup_time=" + backup_time + ", backup_type=" + backup_type + ", backup_path=" + backup_path + ", file_size_mb=" + file_size_mb + ", is_deleted=" + is_deleted + '}';
    }
}
