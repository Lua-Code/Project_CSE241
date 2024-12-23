package Classes;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDate;


public class Admin extends User implements Displayable {
    
    private String role;
    private int workingHours;

    public Admin() {}
    public Admin(String username,String password,LocalDate date,String role, int workingHours) {
        super(username, password, date);
        this.role = role;
        this.workingHours = workingHours;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public int getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }
    
    
    public void display() {
    	super.display();
    }
    
}

