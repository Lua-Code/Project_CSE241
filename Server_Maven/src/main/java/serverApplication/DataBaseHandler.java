package serverApplication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import Classes.*;

public class DataBaseHandler {
	private static DataBaseHandler dbHandler;
	
    public static synchronized DataBaseHandler getDBH() {
        if (dbHandler == null) {
        	dbHandler = new DataBaseHandler();
        }
        return dbHandler;
    }
    
    
    
    //PRODUCT
    public void addProductToCategory(Product p,Category c) {
        if (c != null && p != null) {
            c.addProduct(p);
            return;
        }
        
    }
    
    public Product findProductByCategory(String productName) {
    	for(Category c: getCategories()) {
    		Product p = c.findProduct(productName);
    		if(p != null) {
    			return p;
    		}
    	}
    	return null;
    }
    
    public void deleteProduct(String productName) {
        for (Category c : getCategories()) {
            Product p = c.findProduct(productName);
            if (p != null) {
                c.deleteProduct(p);
                return;
            }
        }
    }
    
    //CATEGORY
    public ArrayList<Category> getCategories() {
        return DataBase.getCategories();
    }
    
    public void addCategory(Category category) {
        DataBase.getCategories().add(category);
    }
    

    public boolean updateCategory(String oldName, Category newCategory) {
        for (int i = 0; i < DataBase.getCategories().size(); i++) {
            if (DataBase.getCategories().get(i).getName().equals(oldName)) {
            	DataBase.getCategories().set(i, newCategory);
                return true;
            }
        }
        return false;
    }

    
    public boolean deleteCategory(String name) {
        Category category = findCategory(name);
        if (category != null) {
            category.getProducts().clear();
            return DataBase.getCategories().remove(category);
        }
        return false;
    }

    public Category findCategory(String input) {
    	for(Category c : DataBase.getCategories()) {
    		if(c.getName().equals(input) || c.getID().equals(input)  ) {
    			return c;
    		}
    	}
    	return null;
    }
    
    public ArrayList<Product> getAllProducts(){
    	ArrayList<Product> products = new ArrayList<Product>();
    	for(Category c : DataBase.getCategories()) {
    		products.addAll(c.getProducts());
    	}
    	return products;
    }
   
    //USER
    
 
    public String deleteUser(String username) {
        for (Iterator<User> iterator = DataBase.getUsers().iterator(); iterator.hasNext(); ) {
            User user = iterator.next();
            if (user.getUsername().equals(username)) {
                iterator.remove();
                return "USER_REMOVAL_SUCCESSFULLY"; 
            }
        }


        return "USER_REMOVAL_FAILED"; 
    }
    
    public User findUser(String username) {
    	for(User u : DataBase.getUsers()) {
    		if(u.getUsername().equals(username)) {
    			return u;
    		}
    	}
    	return null;
    }
    
    public String updateCustomer(String oldUsername,String newUsername,String password,LocalDate birthDate,String address, Gender gender,double balance) {
    	Customer customer = (Customer) findUser(oldUsername);
    	if (customer != null) {
    		customer.setUsername(newUsername);
    		customer.setPassword(password);
    		customer.setBirthDate(birthDate);
    		customer.setAddress(address);
    		customer.setGender(gender);
    		customer.setBalance(balance);
    		return "CUSTOMER_UPDATED_SUCCESSFULLY";
    	}
    	return "CUSTOMER_UPDATED_FAILED";
    }
    
    public String addUser(User u) {
    	DataBase.getUsers().add(u);
    	return "USER_ADDED_SUCCESSFULLY";
    }
    
    
	public Product findItemInCart(String username,String itemName) {
		Customer customer = (Customer)findUser(username);
		Cart cart = customer.getCart();
        for (Map.Entry<Product, Integer> entry : cart.getcartItems().entrySet()) {
            Product product = entry.getKey();
            if(product.getName().equals(itemName)) {
            	return product;
            }
        }
        return null;
	}
	
	public boolean handlePurchase(String username) {
		Customer customer = (Customer)findUser(username);
		if(customer != null) {
			Cart cart = customer.getCart();	
			if(cart.getTotal()<=customer.getBalance()) {
				Order order = new Order(cart.getcartItems(),customer.getUsername(),cart.getTotal());
				customer.addToHistory(order);
				customer.setBalance(customer.getBalance()-cart.getTotal());
				cart.clearCart();
				return true;
			}
		}
		return false;
	}

    public void addItemToCart(String username, Product product, int quantity) {
        Customer customer = (Customer) findUser(username);
        if (customer != null && product != null && quantity > 0) {
            Cart cart = customer.getCart();
            cart.addItem(product, quantity);
        }
    }

    public void removeItemFromCart(String username, String productName) {
        Customer customer = (Customer) findUser(username);
        if (customer != null) {
            Cart cart = customer.getCart();
            Product product = findItemInCart(username, productName);
            if (product != null) {
                cart.removeItem(product);
            }
        }
    }
}
