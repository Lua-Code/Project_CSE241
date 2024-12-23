package Classes;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

@JsonTypeName("Admin")
public class Admin extends User implements Displayable {
    
    private String role;
    private int workingHours;
    private Scanner sc = new Scanner(System.in);

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
    
    
//No need to add user, user creation is up to the user not the Admin
    
    public boolean deleteUser(String username) {
        // Request the database to delete the user
        boolean success = DataBase.deleteUser(username);
        if (success) {
            System.out.println("User successfully deleted.");
        } else {
            System.out.println("User not found or could not be deleted.");
        }
        return success;
    }
    
    public void viewUsers() {
        ArrayList<User> users = DataBase.getUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        for (int i = 0; i < users.size(); i++) {
            System.out.println(users.get(i));
        }
    }
    
    public void searchUser() {
        viewUsers();
        ArrayList<User> users = DataBase.getUsers();
        
        System.out.print("Enter the username of the user to search: ");
        String username = sc.next();
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                System.out.println(users.get(i));
                return;
            }
        }
        System.out.println("User not found.");
    }
    
    public void addProduct() {
        System.out.println("Add Product");
        System.out.println("-----------");
        
        System.out.print("Product Name: ");
        String name = sc.next();
        
        sc.nextLine(); // Consume leftover newline
        System.out.print("Description: ");
        String description = sc.nextLine();
        
        System.out.print("Price: ");
        double price = 0;
        while (true) {
            try {
                price = sc.nextDouble();
                if (price < 0) {
                    System.out.print("Price cannot be negative. Please enter again: ");
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.print("Enter a valid number for price: ");
                sc.next();
            }
        }
        
        System.out.print("Expiration Date (yyyy-MM-dd): ");
        LocalDate expirationDate = null;
        while (expirationDate == null) {
            String dateInput = sc.next();
            try {
                expirationDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date format. Please enter again (yyyy-MM-dd): ");
            }
        }
        
        System.out.print("Category: ");
            String Input = sc.next();
            Category category = DataBase.findCategory(Input);
            if(DataBase.findCategory(Input.toLowerCase()) == null) {
            	System.out.println("Couldn't find category, new one created! ");
            	category = new Category(Input);
                DataBase.addCategory(category);
                
            }

        
        Product newProduct = new Product(name, description, price, expirationDate);
        category.addProduct(newProduct);
        System.out.println("Product added successfully!");
    }
    

        public void deleteProduct() {
        	System.out.print("Enter the name of the Product: ");
        	String input = sc.next();
            for (Category c : DataBase.getCategories()) {
                if(c.findProduct(input.toLowerCase()) != null) {
                	c.deleteProduct(c.findProduct(input));
                	System.out.println("Product Successfully Deleted");
                	return;
                }
                
            }
            System.out.println("Product not found.");
        }

    
    
    public void viewProductInventory() {
        ArrayList<Product> products = DataBase.getAllProducts();
        
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        
        for (Product product : products) {
            product.display();
        }
    }
    
    public void searchProduct() {
        
        System.out.print("Enter the name of the product to search: ");
        String name = sc.next();
        
        for (Category c : DataBase.getCategories()) {
            if(c.findProduct(name) != null) {
                System.out.println("Product found under Category: " + c.getName());
                System.out.println(c.findProduct(name));
                return;
            }
        }
        
        System.out.println("Product not found.");
    }
    
    public void display() {
    	super.display();
    }
    
}

