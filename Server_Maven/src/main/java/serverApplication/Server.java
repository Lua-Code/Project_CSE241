package serverApplication;
import Classes.*;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.*;



public class Server {
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10); 

    public static void main(String[] args) {
        System.out.println("Initiating Server :p");
        try (ServerSocket serverSocket = new ServerSocket(11111)) {
            System.out.println("Server is running and waiting for clients!");
            createDummyData();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client "+clientSocket.getInetAddress()+" Connected!");
                ServerClientHandler serverclientHandler = new ServerClientHandler(clientSocket);

                threadPool.execute(serverclientHandler);
            }
        } catch (IOException e) {
            System.err.println("Server failed to start :(, ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
        	threadPool.shutdown();
        }
    }
    
    
    
    public static void createDummyData() {
        ArrayList<Category> categories = DataBase.getCategories();
    	ArrayList<User> users  = DataBase.getUsers();
        // Category 1: Electronics
        Category electronics = new Category("Electronics");
        electronics.addProduct(new Product("Laptop", "High performance laptop", 999.99, LocalDate.of(2025, 12, 31)));
        electronics.addProduct(new Product("Smartphone", "Latest smartphone with great features", 799.99, LocalDate.of(2024, 6, 15)));
        electronics.addProduct(new Product("Headphones", "Noise-cancelling headphones", 199.99, LocalDate.of(2026, 8, 20)));
        electronics.addProduct(new Product("Smartwatch", "Stylish smartwatch with health tracking", 149.99, LocalDate.of(2024, 11, 1)));
        electronics.addProduct(new Product("Keyboard", "Mechanical keyboard with RGB lighting", 89.99, LocalDate.of(2025, 5, 10)));

        // Category 2: Clothing
        Category clothing = new Category("Clothing");
        clothing.addProduct(new Product("T-shirt", "Comfortable cotton t-shirt", 19.99, LocalDate.of(2026, 1, 15)));
        clothing.addProduct(new Product("Jeans", "Stylish blue jeans", 39.99, LocalDate.of(2025, 9, 30)));
        clothing.addProduct(new Product("Jacket", "Warm winter jacket", 69.99, LocalDate.of(2025, 11, 10)));
        clothing.addProduct(new Product("Sneakers", "Running shoes for comfort", 49.99, LocalDate.of(2024, 7, 25)));
        clothing.addProduct(new Product("Hat", "Fashionable baseball cap", 14.99, LocalDate.of(2024, 10, 5)));

        // Category 3: Groceries
        Category groceries = new Category("Groceries");
        groceries.addProduct(new Product("Milk", "Fresh cow's milk", 1.99, LocalDate.of(2024, 5, 20)));
        groceries.addProduct(new Product("Eggs", "Dozen free-range eggs", 3.49, LocalDate.of(2024, 5, 18)));
        groceries.addProduct(new Product("Bread", "Whole grain bread", 2.49, LocalDate.of(2024, 5, 15)));
        groceries.addProduct(new Product("Butter", "Organic butter", 4.99, LocalDate.of(2024, 6, 1)));
        groceries.addProduct(new Product("Cheese", "Cheddar cheese block", 5.99, LocalDate.of(2024, 6, 10)));

        // Category 4: Home Appliances
        Category appliances = new Category("Home Appliances");
        appliances.addProduct(new Product("Blender", "High-power blender for smoothies", 59.99, LocalDate.of(2025, 3, 1)));
        appliances.addProduct(new Product("Microwave", "Compact microwave oven", 89.99, LocalDate.of(2024, 12, 25)));
        appliances.addProduct(new Product("Vacuum Cleaner", "Cordless vacuum cleaner", 129.99, LocalDate.of(2025, 1, 10)));
        appliances.addProduct(new Product("Washing Machine", "Automatic washing machine", 349.99, LocalDate.of(2026, 6, 20)));
        appliances.addProduct(new Product("Toaster", "Two-slice toaster", 24.99, LocalDate.of(2025, 8, 15)));

        // Category 5: Books
        Category books = new Category("Books");
        books.addProduct(new Product("Java Programming", "Comprehensive guide to Java programming", 29.99, LocalDate.of(2025, 10, 1)));
        books.addProduct(new Product("The Great Gatsby", "Classic American novel", 14.99, LocalDate.of(2024, 7, 25)));
        books.addProduct(new Product("The Hobbit", "Fantasy novel by J.R.R. Tolkien", 19.99, LocalDate.of(2025, 5, 30)));
        books.addProduct(new Product("1984", "Dystopian novel by George Orwell", 9.99, LocalDate.of(2024, 8, 15)));
        books.addProduct(new Product("Clean Code", "Book on software craftsmanship", 34.99, LocalDate.of(2025, 2, 10)));

        // Category 6: Toys
        Category toys = new Category("Toys");
        toys.addProduct(new Product("Lego Set", "Lego set for building houses", 49.99, LocalDate.of(2025, 12, 31)));
        toys.addProduct(new Product("Action Figure", "Superhero action figure", 19.99, LocalDate.of(2024, 6, 10)));
        toys.addProduct(new Product("Puzzle", "1000-piece puzzle", 14.99, LocalDate.of(2025, 3, 1)));
        toys.addProduct(new Product("Doll", "Fashion doll with accessories", 29.99, LocalDate.of(2024, 9, 20)));
        toys.addProduct(new Product("Remote Control Car", "RC car with 2-hour battery life", 39.99, LocalDate.of(2024, 11, 5)));

        // Add categories to the list
        categories.add(electronics);
        categories.add(clothing);
        categories.add(groceries);
        categories.add(appliances);
        categories.add(books);
        categories.add(toys);
        
        Admin admin1 = new Admin( "2", "2", LocalDate.of(1980, 5, 15),"Supervisor",9);
        Admin admin2 = new Admin( "admin2", "admin456", LocalDate.of(1975, 3, 20),"Manager",12);
        
        users.add(admin1);
        users.add(admin2);
        
        for (int i = 1; i <= 12; i++) {
            Customer customer = new Customer("customer" + i, "password" + i, LocalDate.of(2000, 1, i),"Dummy Address",Gender.MALE,19283);
            customer.getCart().addItem(new Product("Lego Set", "Lego set for building houses", 49.99, LocalDate.of(2025, 12, 31)),2);
            users.add(customer);
        }
        users.add(new Customer("1", "1", LocalDate.of(2000, 1, 1),"Dummy Address",Gender.MALE,19283));

    }
}
