package client;
import java.io.IOException;
import java.time.LocalDate;
import Classes.Gender;
public class TestClient {
    private LocalClientHandler client;

    // Initialize the client with a mock user
    public void setUp() throws IOException {
        client = new LocalClientHandler("localhost", 11111);
        client.connect();// Assuming the server is running on localhost with port 11111
    }

    public void logout() throws IOException {
        client.disconnect();// Assuming the server is running on localhost with port 11111
    }

    // Test case: Client adding a product to the cart
    public void testAddProductToCart() throws IOException {
        // Assuming the client has a method for adding products to the cart
        Response response = client.addToCart("Laptop", 2);  // Add 2 laptops
        System.out.println(response);  // Expected: "Product added to cart successfully"
    }

    // Test case: Client trying to make a purchase
    public void testPurchase() throws IOException {
        // Assuming the client has a method for making a purchase
        Response response = client.requestPurchase("customer1");
        System.out.println(response);  // Expected: "Purchase successful"
    }

    // Test case: Admin adding a product (Admin functionality)
    public void testAdminAddProduct() throws IOException {
        // Assuming we can login as admin
        client.login("admin1", "admin123");
        Response response = client.addProduct("Electronics", "Tablet", "High-performance tablet", 499.99, LocalDate.of(2025, 12, 31));
        System.out.println(response);  // Expected: "Product added successfully"
    }

    // Test case: Admin updating a product
    public void testAdminUpdateProduct() throws IOException {
        // Admin login
        client.login("admin1", "admin123");
        // Admin updates a product
        Response response = client.updateProduct("Laptop", "Gaming Laptop", "High-end gaming laptop", 1299.99, LocalDate.of(2026, 12, 31));
        System.out.println(response);  // Expected: "Product updated successfully"
    }

    // Test case: Admin deleting a product
    public void testAdminDeleteProduct() throws IOException {
        // Admin login
        client.login("admin1", "admin123");
        // Admin deletes a product
        Response response = client.deleteProduct("Smartphone");
        System.out.println(response);  // Expected: "Product deleted successfully"
    }

    // Test case: Admin adding a category
    public void testAdminAddCategory() throws IOException {
        // Admin login
        client.login("admin1", "admin123");
        // Admin adds a category
        Response response = client.addCategory("Smart Gadgets", "Latest smart gadgets like wearables and AI assistants", "SG123");
        System.out.println(response);  // Expected: "Category added successfully"
    }

    // Test case: Admin deleting a category
    public void testAdminDeleteCategory() throws IOException {
        // Admin login
        client.login("kevindavid", "12345678");
        // Admin deletes a category
        Response response = client.deleteCategory("Old Gadgets");
        System.out.println(response);  // Expected: "Category deleted successfully"
    }

    // Test case: Viewing products in a specific category
    public void testViewProductsByCategory() throws IOException {
        // Assume the client can view products by category
        Response response = client.viewProductsByCategory("Electronics");
        System.out.println(response);  // Expected: "Displaying products in Electronics"
    }

    public void getCategories() throws IOException {
        // Assume the client can view products by category
        Response response = client.getCategories();
        System.out.println(response);  // Expected: "Displaying products in Electronics"
    }

    // Test case: Client signup
    public void testSignUp() throws IOException {
        // Test user signup
        Response response = client.signUp("new_user", "new_password", LocalDate.of(1990, 1, 1), "New Street", Gender.FEMALE, 50.0);
        System.out.println(response);  // Expected: "User signed up successfully"
    }

    public void testLogin() throws IOException {
        // Test user signup
        Response response = client.login("customer1", "password1");
        System.out.println("Login request sent");
        System.out.println(response);  // Expected: "User signed up successfully"
        System.out.println("Login request recieved");
    }

    // Running all the tests
    public static void main(String[] args) {
        TestClient testClient = new TestClient();

        try {
            testClient.setUp();
            testClient.testLogin();
            testClient.getCategories();
//            testClient.testSignUp();
//            testClient.testAddProductToCart();
//            testClient.testPurchase();
//            testClient.testAdminAddProduct();
//            testClient.testAdminUpdateProduct();
//            testClient.testAdminDeleteProduct();
//            testClient.testAdminAddCategory();
//            testClient.testAdminDeleteCategory();
//            testClient.testViewProductsByCategory();

            testClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}