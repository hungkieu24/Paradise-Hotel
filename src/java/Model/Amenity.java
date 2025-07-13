/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author hungk
 */
public class Amenity {
    private int id;
    private String name;
    private String description;
    private int branchId;
    private boolean is_deleted;

    public Amenity() {
    }

    public Amenity(int id, String name, String description, int branchId, boolean is_deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branchId = branchId;
        this.is_deleted = is_deleted;
    }

    public Amenity(String name, String description, int branchId) {
        this.name = name;
        this.description = description;
        this.branchId = branchId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return "Amenity{" + "id=" + id + ", name=" + name + ", description=" + description + ", branchId=" + branchId + ", is_deleted=" + is_deleted + '}';
    }

    
}
