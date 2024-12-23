package serverApplication;

import java.util.ArrayList;

import Classes.*;

public class DataBase {

    private static ArrayList<User> users = new ArrayList<User>();
    private static ArrayList<Category> categories = new ArrayList<Category>();
    

    public static ArrayList<Category> getCategories() {
        return categories;
    }
    
    public static ArrayList<User> getUsers() {
        return users;
    }
    
    
    
}
