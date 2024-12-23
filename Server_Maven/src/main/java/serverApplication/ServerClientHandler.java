package serverApplication;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import Classes.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.NullNode;



class UserDeserializer extends JsonDeserializer<User> {

	@Override
	public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		// Check if the input is null
		if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
			return null; // Return null if the user is null
		}

		// Otherwise, proceed with the regular deserialization logic
		return p.readValueAs(User.class); // Deserialize as usual
	}
}


//This class ya regala focuses on HANDLING REQUESTS mn el clients,
//Input:Request mn el clienthandler with a specific format
//Output:Response back to the user, like LoginSuccessful or LoginFailed
//Format we will use fl project for request is: Action|Parameter1|Parameter2|Parameter3 etc.
//Ex. GUIButtonclickevent->UIHandler->LocalClientHandler->Server->ServerClientHandler->other Handlers->ServerClientHandler->Server->LocalClientHandler->UIHandler

public class ServerClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private DataBaseHandler dbHandler;
	private ObjectMapper mapper;

    public ServerClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.dbHandler = DataBaseHandler.getDBH();
		this.mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addKeySerializer(Product.class, new ProductKeySerializer());
		module.addKeyDeserializer(Product.class, new ProductKeyDeserializer());
		this.mapper.registerModules(new JavaTimeModule(), new SimpleModule().addDeserializer(User.class, new UserDeserializer()),module);
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() { //listens to requests from the user, SIMPLE AF,run override 34ashan we are using threads for multiple clients at once
        try {
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("Received request: " + request);
				handleRequest(request);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //USER RESPONSES
    
    public boolean checkAdminAccess(User user) {
    	return (user instanceof Admin);
    }
    
    public Response checkLogin(String UM, String PW) throws JsonProcessingException {
        User user = dbHandler.findUser(UM);
        if (user != null) {
            if (user.getPassword().equals(PW)) {
				String serializedUser = mapper.writeValueAsString(user);
                return new Response("LOGIN_SUCCESSFUL",Map.of("user",serializedUser));
            } else {
                return new Response("INCORRECT_CREDENTIALS",Map.of());
            }
        }
        return new Response("USER_NOT_FOUND",Map.of());
    }
    
	public Response signUp(String username,String password,LocalDate birthDate,String address, Gender gender,double balance) { // ask for user name and password, then check that username doesnt exist in the database, if not then ask for password and save user in database.
		if(dbHandler.findUser(username) != null) {
			return new Response("USER_ALREADY_EXISTS",Map.of());
		}
		dbHandler.addUser(new Customer(username,password,birthDate,address,gender,balance));
		return new Response("SIGNUP_SUCCESSFUL",Map.of());
		
	}
	
	public Response updateCustomer(String oldUsername,String newUsername,String password,LocalDate birthDate,String address, Gender gender,double balance) {
		return new Response(dbHandler.updateCustomer(oldUsername,newUsername,password,birthDate,address,gender,balance),Map.of());
	}
	public Response getUser(String username) throws JsonProcessingException {
		User user = dbHandler.findUser(username);
		if (user != null) {
			String serializedUser = mapper.writeValueAsString(user);
			return new Response("USER_FETCHED",Map.of("user",serializedUser));
		}
		return new Response("USER_NOT_FOUND",Map.of());
	}
	
	public Response deleteUser(String username) {
		return new Response(dbHandler.deleteUser(username),Map.of());
	}
    
	public Response addProduct(String categoryName, String name, String description, double price, LocalDate expirationDate) {
		Category category = dbHandler.findCategory(categoryName);
		Product p = new Product(name,description,price,expirationDate);
		if(category != null) {
			dbHandler.addProductToCategory(p, category);
			return new Response("PRODUCT_ADDED_SUCCESSFULLY",Map.of("newCategories",dbHandler.getCategories()));
		}
		else {
			category = new Category(categoryName);
			dbHandler.addCategory(category);
			dbHandler.addProductToCategory(p, category);
			return new Response("PRODUCT_ADDED_TO_NEW_CATEGORY",Map.of("newCategory",category));
			
		}
	}
	
	public Response deleteProduct(String name) {
		Product p = dbHandler.findProductByCategory(name);
		if(p != null) {
			dbHandler.deleteProduct(name);
			return new Response("PRODUCT_REMOVAL_SUCCESSFUL",Map.of("newCategories",dbHandler.getCategories()));
		}
		return new Response("PRODUCT_REMOVAL_FAILED",Map.of());
	}
	
	public Response viewProductsByCategory(String categoryName) {
		Category c = dbHandler.findCategory(categoryName);
		if(c != null) {
			ArrayList<Product> products = c.getProducts();
			return new Response("SUCCESS",Map.of("products",products));
		}
		return new Response("CATEGORY_NOT_FOUND",Map.of());
	}
	
	public Response viewAllProducts() {
		return new Response("SUCCESS",Map.of("products",dbHandler.getAllProducts()));
	}

	public Response updateProduct(String oldName,String newName,String description,double price,LocalDate expirationdate) {
		Product p = dbHandler.findProductByCategory(oldName);
		if(p != null) {
			p.setName(newName);
			p.setDescription(description);
			p.setPrice(price);
			p.setExpirationDate(expirationdate);
			return new Response("PRODUCT_UPDATE_SUCCESSFULLY",Map.of());
		}
		return new Response("PRODUCT_UPDATE_FAILED",Map.of());
	}
	
	public Response getCategories() {
		return new Response("SUCCESS",Map.of("categories",dbHandler.getCategories()));
	}
	
	public Response deleteCategory(String categoryName) {
		Category c = dbHandler.findCategory(categoryName);
		if(c != null) {
			dbHandler.deleteCategory(categoryName);
			return new Response("CATEGORY_DELETION_SUCCESSFULLY",Map.of());
		}
		return new Response("CATEGORY_DELETION_SUCCESSFULLY",Map.of());
	}
	
	public Response addCategory(String name,String description,String id) {
		dbHandler.addCategory(new Category(name,description,id));
		return new Response("CATEGORY_ADDED_SUCCESSFULLY",Map.of());
	}

	public Response updateCategory(String Oldname,String newName,String description,String id) {
		Category c = dbHandler.findCategory(Oldname);
		if(c != null) {
			c.setName(newName);
			c.setDescription(description);
			c.setID(id);
			return new Response("CATEGORY_UPDATE_SUCCESSFUL",Map.of());
		}
		return new Response("CATEGORY_UPDATE_FAILED",Map.of());
	}
	
	public Response clearCart(String username) {
		Customer customer = (Customer) dbHandler.findUser(username);
		if(customer != null) {
			customer.getCart().clearCart();
			return new Response("CART_CLEARED",Map.of());	
		}
		return new Response("CART_CLEAR_FAILED",Map.of());

	}
	
	public Response removeCartItem(String username,String itemName) {
		Customer customer = (Customer) dbHandler.findUser(username);
		Product item = dbHandler.findItemInCart(username, itemName);
		if(customer != null) {
			dbHandler.removeItemFromCart(username,itemName);
			return new Response("CART_ITEM_REMOVED",Map.of("newCart",customer.getCart()));
		}
		return new Response("CART_ITEM_NOT_REMOVED",Map.of());

	}
	
	public Response addCartItem(String username,String itemName,int quantity) {
		Customer customer = (Customer) dbHandler.findUser(username);
		Product item = dbHandler.findProductByCategory(itemName).clone();
		if(customer != null) {
			customer.getCart().addItem(item,quantity);
			return new Response("CART_ITEM_ADDED",Map.of("newCart",customer.getCart()));
		}
		return new Response("CART_ITEM_NOT_ADDED",Map.of());
	}

	public Response getCart(String username) {
		Customer customer = (Customer) dbHandler.findUser(username);
		if(customer != null) {
			return new Response("CART_ITEM_LIST",Map.of("newCart",customer.getCart()));
		}
		return new Response("CART_NOT_FOUND",Map.of());
	}
	
	
	public Response intitiatePurchase(String username) {
		Customer customer = (Customer)dbHandler.findUser(username);
		if(dbHandler.handlePurchase(username)) {
			return new Response("PURCHASE_SUCCESSFUL",Map.of("newHistory",customer.getHistory()));
		}
		return new Response("PURCHASE_FAILED",Map.of());
	}
	
	private void handleRequest(String jsonRequest) throws JsonProcessingException {
    	Request recievedRequest = mapper.readValue(jsonRequest, Request.class);
        String action = recievedRequest.getAction();
        String status;
        Map<String,Object> data = recievedRequest.getData();
        User requestee = recievedRequest.getRequestee();
        Response response = new Response("UNKNOWN_ERROR",Map.of()); 
        
        
        switch (action) {
        
        //USER STUFF
        case "LOGIN": //read user R in CRUD
        	response = checkLogin((String)data.get("username"), (String)data.get("password"));
            break;

        case "SIGNUP":
			String signUpusername = (String)data.get("username");
			String signUppassword = (String)data.get("password");
			String signUpaddress = (String)data.get("address");
			Gender signUpgender = Gender.valueOf(((String) data.get("gender")).toUpperCase());
			Double signUpbalance = (Double)data.get("balance");
			List<Integer> signUpbirthdateList = (List<Integer>) data.get("birthdate");
			LocalDate signUpbirthdate = LocalDate.of(
					signUpbirthdateList.get(0), // year
					signUpbirthdateList.get(1), // month
					signUpbirthdateList.get(2)  // day
			);

        	response = signUp(signUpusername,signUppassword,signUpbirthdate,signUpaddress,signUpgender,signUpbalance);
            break;
        
        case "UPDATE_USER":
			// Extract and convert data from the map
			String oldUsername = (String) data.get("oldUsername");
			String newUsername = (String) data.get("newUsername");
			String password = (String) data.get("password");
			String address = (String) data.get("address");
			Gender gender = Gender.valueOf(((String) data.get("gender")).toUpperCase());
			Double balance = (Double) data.get("balance");

			List<Integer> birthdateList = (List<Integer>) data.get("birthdate");
			LocalDate birthdate = LocalDate.of(
					birthdateList.get(0), // year
					birthdateList.get(1), // month
					birthdateList.get(2)  // day
			);

			response = updateCustomer(oldUsername, newUsername, password, birthdate, address, gender, balance);

        	break;

		case "GET_USER":
			response = getUser((String)data.get("username"));

			break;
            
        case "DELETE_USER": //delete user
        	if(checkAdminAccess(requestee)) {
        		status = dbHandler.deleteUser((String)data.get("username"));
        		response = new Response(status,Map.of());
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	break;
        
        //PRODUCT STUFF
        case "ADD_PRODUCT":
        	if(checkAdminAccess(requestee)) {
				String categoryName = (String) data.get("categoryname");
				String Name = (String) data.get("name");
				String description = (String) data.get("description");
				double price = (Double) data.get("price");
				List<Integer> expirationDateList = (List<Integer>) data.get("expirationdate");
				LocalDate expirationDate = LocalDate.of(
						expirationDateList.get(0), // year
						expirationDateList.get(1), // month
						expirationDateList.get(2)  // day
				);

        		response = addProduct(categoryName,Name,description,price,expirationDate);
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	break;
        	
        case "DELETE_PRODUCT":
        	if(checkAdminAccess(requestee)) {
        		response = deleteProduct((String)data.get("name"));
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	break;
        	
        case "VIEW_PRODUCTS_BY_CATEGORY":
           	response = viewProductsByCategory((String)data.get("categoryname"));
        	break;
        	
        case "VIEW_ALL_PRODUCTS":
        	response = viewAllProducts();
        	break;
        	
        case "UPDATE_PRODUCT":
           	if(checkAdminAccess(requestee)) {
				String oldName = (String) data.get("oldname");
				String newName = (String) data.get("newname");
				String description = (String) data.get("description");
				double price = (Double) data.get("price");
				List<Integer> expirationDateList = (List<Integer>) data.get("expirationdate");
				LocalDate expirationDate = LocalDate.of(
						expirationDateList.get(0), // year
						expirationDateList.get(1), // month
						expirationDateList.get(2)  // day
				);
           		response = updateProduct(oldName,newName,description,price,expirationDate);
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	
        	break;
        
        //CATEGORY STUFF
        case "GET_CATEGORIES":
        	response = getCategories();
        	break;
        	
        case "DELETE_CATEGORY":
           	if(checkAdminAccess(requestee)) {
           		response = deleteCategory((String)data.get("categoryname"));
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	break;
        	
        case "ADD_CATEGORY":
           	if(checkAdminAccess(requestee)) {
           		response = addCategory((String)data.get("name"),(String)data.get("description"),(String)data.get("id"));
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	
        	break;
        	
        case "UPDATE_CATEGORY":
           	if(checkAdminAccess(requestee)) {
           		response = updateCategory((String)data.get("oldname"),(String)data.get("newname"),(String)data.get("description"),(String)data.get("id"));
        	}
        	else {
        		response = new Response("ACCESS_DENIED",Map.of());
        	}
        	
        	break;
        	
        case "CLEAR_CART":
        	response = clearCart(requestee.getUsername());
        	break;
        	
        case "ADD_ITEM":
        	response = addCartItem(requestee.getUsername(), (String)data.get("productname"),(Integer)data.get("quantity"));
        	break;
        	
        case "REMOVE_ITEM":
        	response = removeCartItem(requestee.getUsername(), (String)data.get("productname"));
        	break;
        	
        case "REQUEST_PURCHASE":
        	response = intitiatePurchase(requestee.getUsername());
        	break;

		case "GET_CART":
			response = getCart(requestee.getUsername());
			break;
        	
        default:
        	response = new Response("UNKNOWN_ACTION",Map.of());
            break;
        }
		String json = mapper.writeValueAsString(response);
		System.out.println(json);
        out.println(json);
    }

	
}
