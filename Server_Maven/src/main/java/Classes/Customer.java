package Classes;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDate;
import java.util.ArrayList;


public class Customer extends User implements Displayable  {

    private double balance;
    private String address;
    private Gender gender;
    private ArrayList<String> interests;
    private Cart cart;
    private ArrayList<Order> history;

    public Customer() {}
    public Customer(String username,String password,LocalDate birthDate,String address, Gender gender,double balance) { 
        super(username,password,birthDate);
        this.balance = balance;
        this.address = address;
        this.gender = gender;
        this.interests = new ArrayList<String>();
        this.cart = new Cart();
        this.history = new ArrayList<Order>();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void addInterest(String interest) { //Changed add interest to add interest ;p
        this.interests.add(interest);
    }

    public void setInterests(ArrayList<String> interests) { 
        this.interests = interests;
    }

    public Cart getCart() {
        return cart;
    }
    
    public void viewCart() {
    	getCart().viewCart();
    }


    public ArrayList<Order> getHistory() {
        return history;
    }
    
    public void addToHistory(Order order) {
        getHistory().add(order);
    }
   
    
    public void display() {
    	super.display();
    	System.out.println("Gender: "+this.getGender());
    	System.out.println("Balance: $"+this.getBalance());
    	System.out.println("Address: "+this.getAddress());
    	
    }



}

