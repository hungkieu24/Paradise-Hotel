/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;

/**
 *
 * @author hungk
 */
public class Expense {
    private int id;
    private int branch_id;
    private String expense_type;
    private double amount;
    private String description;
    private Date expense_date;
    private String created_by;

    public Expense() {
    }

    public Expense(int id, int branch_id, String expense_type, double amount, String description, Date expense_date, String created_by) {
        this.id = id;
        this.branch_id = branch_id;
        this.expense_type = expense_type;
        this.amount = amount;
        this.description = description;
        this.expense_date = expense_date;
        this.created_by = created_by;
    }


    public Expense(int branch_id, String expense_type, double amount, String description, Date expense_date) {
        this.branch_id = branch_id;
        this.expense_type = expense_type;
        this.amount = amount;
        this.description = description;
        this.expense_date = expense_date;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public String getExpense_type() {
        return expense_type;
    }

    public void setExpense_type(String expense_type) {
        this.expense_type = expense_type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpense_date() {
        return expense_date;
    }

    public void setExpense_date(Date expense_date) {
        this.expense_date = expense_date;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    @Override
    public String toString() {
        return "Expense{" + "id=" + id + ", branch_id=" + branch_id + ", expense_type=" + expense_type + ", amount=" + amount + ", description=" + description + ", expense_date=" + expense_date + ", created_by=" + created_by + '}';
    }
}
