package Classes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Map;

public class Order implements Displayable {

    private Map<Product,Integer>  products;
    @JsonProperty("cusmtomerName")
    private String customerName;
    private LocalDate date;
    private double total;

    public Order(){}

    public Order(Map<Product,Integer> products, String customerName,double total) {
        this.products = products;
        this.customerName = customerName;
        this.date = LocalDate.now(); 
        this.total = total;
    }

    public Map<Product,Integer>  getProducts() {
        return products;
    }

    public void setProducts(Map<Product,Integer>  products) {
        this.products = products;
    }

    public String getCusmtomerName() {
        return customerName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public double getTotal() {
    	return this.total;
    }
    
    public void display() {
    	System.out.println("Products: ");
        for (Map.Entry<Product, Integer> entry : getProducts().entrySet()) {
            entry.getKey().display();
        }
    	System.out.println("Total Price: "+getTotal());
    	System.out.println("Date: " + getDate());
    	System.out.println("---------------------------------");
    }

}

