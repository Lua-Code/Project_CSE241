package Classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Admin.class, name = "Admin"),
        @JsonSubTypes.Type(value = Customer.class, name = "Customer")
})
public abstract class User implements Displayable {


    private String username;
    private String password;
    private LocalDate birthDate;

    public User() {}

    public User(String username, String password, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public boolean validatePassword(String pw) {
    	return pw.equals(this.getPassword());	
    }


    public void display() {
    	System.out.println("---------------------------------");
		System.out.println("User Type:"+ this.getClass().getSimpleName());
    	System.out.println("---------------------------------");
    	System.out.println("Username: "+ getUsername());
    	System.out.println("Password: "+ getPassword());
    	System.out.println("Birth Date: "+ getBirthDate());
    	
    }
    
    


}

