/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Dal.RoomTypeDAO;
import Model.RoomType;
import java.util.List;
import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author thien
 */

public class Main {
    

    public static void main(String[] args) {
        String hashed = "$2a$12$TSDFrHdEsG56OuqNXuQpWeZEG7yOoBWHeizccaBWI06Av84IHM1/W";
        String pass = "hashed_password5";
        System.out.println(BCrypt.hashpw(pass, BCrypt.gensalt(12)));
       
    }
}
