/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Dal.RoomTypeDAO;
import java.util.List;

/**
 *
 * @author hungk
 */
public class RoomType {
    private int roomTypeID;
    private String name;
    private String description;
    private double base_price;
    private int capacity_adult;// sửa thành trẻ em và người lớn
    private int capacity_child;
    private String image_url;

    public RoomType() {
    }

    public RoomType(int roomTypeID, String name, String description, double base_price, int capacity_adult, int capacity_child, String image_url) {
        this.roomTypeID = roomTypeID;
        this.name = name;
        this.description = description;
        this.base_price = base_price;
        this.capacity_adult = capacity_adult;
        this.capacity_child = capacity_child;
        this.image_url = image_url;
    }

    public RoomType(String name, String description, double base_price, int capacity_adult, int capacity_child, String image_url) {
        this.name = name;
        this.description = description;
        this.base_price = base_price;
        this.capacity_adult = capacity_adult;
        this.capacity_child = capacity_child;
        this.image_url = image_url;
    }

    public int getRoomTypeID() {
        return roomTypeID;
    }

    public void setRoomTypeID(int roomTypeID) {
        this.roomTypeID = roomTypeID;
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

    public double getBase_price() {
        return base_price;
    }

    public void setBase_price(double base_price) {
        this.base_price = base_price;
    }

    public int getCapacity_adult() {
        return capacity_adult;
    }

    public void setCapacity_adult(int capacity_adult) {
        this.capacity_adult = capacity_adult;
    }

    public int getCapacity_child() {
        return capacity_child;
    }

    public void setCapacity_child(int capacity_child) {
        this.capacity_child = capacity_child;
    }

    

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "RoomType{" + "roomTypeID=" + roomTypeID + ", name=" + name + ", description=" + description + ", base_price=" + base_price + ", capacity_adult=" + capacity_adult + ", capacity_child=" + capacity_child + ", image_url=" + image_url + '}';
    }

    
   
}
