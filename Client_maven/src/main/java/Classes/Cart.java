package Classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore unknown properties like "empty"
public class Cart {

    private Map<Product,Integer> cartItems;
    private double total;

    public Cart() {
    	cartItems = new HashMap<>();
    	total = 0;
    }
    
    public Map<Product,Integer> getcartItems() {
        return cartItems;
    }

    public void addItem(Product product, int quantity) {
    		if (getcartItems().containsKey(product)) {
    			getcartItems().put(product, cartItems.get(product) + quantity);
    		} else {
    			getcartItems().put(product, quantity);
    		}
    		updateTotal();
    	}
    
    public void removeItem(Product product) {
    	if(getcartItems().containsKey(product)) {
    		getcartItems().remove(product);
            updateTotal();
    	}
    }
    
    public void viewCart() {
    	int x = 1;
        if (isEmpty()) {
            System.out.println("Cart is empty. :(");
            return;
        }
        System.out.println("Cart Items:");
        System.out.println("----------------");
        for (Map.Entry<Product, Integer> entry : getcartItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(x++ +"." + product.getName() + " x" + quantity);
        }
        System.out.println("----------------");
        System.out.println("Cart Total: $" + getTotal());
    }
    
    public void clearCart() {
        getcartItems().clear();
        System.out.println("Cart has been cleared. ;)");
    }
    
    public boolean isEmpty() {
    	return cartItems.isEmpty();
    }

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}
	
	public void updateTotal() {
		double temp = 0;
        for (Map.Entry<Product, Integer> entry : getcartItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
    		temp += quantity * product.getPrice();
        }
        setTotal(Math.round(temp*100.0)/100.0);
	}
	
	

}

