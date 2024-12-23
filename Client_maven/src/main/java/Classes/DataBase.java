package Classes;

import java.util.ArrayList;
import java.util.Iterator;

public class DataBase {

    private static ArrayList<User> users = new ArrayList<User>();
    private static ArrayList<Category> categories = new ArrayList<Category>();
    

    //CATEGORY
    
    public static void addCategory(Category category) {
        categories.add(category);
    }
    
    
    public static ArrayList<Category> getCategories() {
        return categories;
    }

    
    public static boolean updateCategory(String oldName, Category newCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(oldName)) {
                categories.set(i, newCategory);
                return true;
            }
        }
        return false;
    }

    
    public static boolean deleteCategory(String name) {
        return categories.removeIf(category -> category.getName().equals(name));
    }
    public static Category findCategory(String input) {
    	for(Category c : categories) {
    		if(c.getName().equals(input) || c.getID().equals(input)  ) {
    			return c;
    		}
    	}
    	return null;
    }
    
    public static ArrayList<Product> getAllProducts(){
    	ArrayList<Product> products = new ArrayList<Product>();
    	for(Category c : getCategories()) {
    		products.addAll(c.getProducts());
    	}
    	return products;
    }
   
    //USER
    
    public static ArrayList<User> getUsers() {
        return users;
    }
    
    
    public static boolean deleteUser(String username) {
        for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
            User user = iterator.next();
            if (user.getUsername().equals(username)) {
                iterator.remove();
                return true; 
            }
        }


        return false; 
    }
    
    public static User findUser(String username) {
    	for(User u : getUsers()) {
    		if(u.getUsername().equals(username)) {
    			return u;
    		}
    	}
    	return null;
    }
    
    public static void addUser(User u) {
    	users.add(u);
    }
    
    
    
}
