package Classes;

import java.time.LocalDate;

public class Product implements Displayable {

    private String name;
    private String description;
    private double price;
    private LocalDate expirationDate;

    public Product() {}
    public Product(String name, String description, double price, LocalDate expirationDate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.expirationDate = expirationDate;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    public void display(){
    	System.out.println("Name: "+ getName());
    	System.out.println("Price: $"+getPrice());
    	System.out.println("Expiration Date: " + getExpirationDate());
    	System.out.println("Description: "+ getDescription());
    	System.out.println();
    	
    }
    public String toStringKey() {
        return name + "|" + description + "|" + price + "|" + expirationDate;
    }

    public static Product fromStringKey(String key) {
        String[] parts = key.split("\\|");
        return new Product(
                parts[0],                              // name
                parts[1],                              // description
                Double.parseDouble(parts[2]),          // price
                LocalDate.parse(parts[3])              // expirationDate
        );
    }
    
    @Override
    public Product clone() {
    	return new Product(getName(),getDescription(),getPrice(),getExpirationDate());
    }
}
