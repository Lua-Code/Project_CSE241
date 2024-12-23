package client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Classes.*;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Map;

public class LocalClientHandler {
    private final String serverAddress;
    private final int serverPort;
    private User currentUser;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectMapper mapper; //we will use jackson library to serialize and deserialize our data requests ex. data -> json format -> server -> data -> do stuff ->  json format ->client -> data

    public LocalClientHandler(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addKeySerializer(Product.class, new ProductKeySerializer());
        module.addKeyDeserializer(Product.class, new ProductKeyDeserializer());
        this.mapper.registerModules(module,new JavaTimeModule());
        this.currentUser = null; //anonymous subclass instantiation
    }
    
    //NETWORK FUNCTIONALITIES

    public User getCurrentUser() {
		return currentUser;
	}
    public void updateCurrentUser() throws IOException {
       setCurrentUser(getUser(currentUser.getUsername()));
    }

	public void setCurrentUser(User newUser) {
		this.currentUser = newUser;
	}



	public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }


    private Response sendRequest(Request request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        try {
            String requestJson = mapper.writeValueAsString(request);
            out.println(requestJson);

            String jsonResponse = in.readLine();

            if (jsonResponse == null) {
                throw new IOException("Received null response from server");
            }

            Response response = mapper.readValue(jsonResponse, Response.class);

            if (response == null) {
                throw new IOException("Failed to parse server response into a valid Response object");
            }

            return response;
        } catch (IOException e) {
            System.err.println("Error during request/response handling: " + e.getMessage());
            e.printStackTrace();

            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("An unexpected error occurred while processing the request", e);
        }
    }

    
    //USER REQUESTS

    public User getUser(String username) throws IOException {
        Request request = new Request("GET_USER", Map.of("username", username), getCurrentUser());
        Response response = sendRequest(request);
        Map<String, String> data = mapper.convertValue(response.getData(), new TypeReference<>() {});
        if (data != null) {
            if (data.containsKey("user")) {
                String userJson = data.get("user");
                User deserializedUser = mapper.readValue(userJson, User.class);
                User user = mapper.readValue(userJson, User.class);  // Deserialize with the correct type
                return user;
            }
            return null;
        }
        return null;
    }

    public Response login(String username, String password) throws IOException {
        Request request = new Request("LOGIN", Map.of("username", username, "password", password), getCurrentUser());
        Response response = sendRequest(request);
        Map<String, String> data = mapper.convertValue(response.getData(), new TypeReference<>() {});

        if (data != null) {
            if (data.containsKey("user")) {
                String userJson = data.get("user");
                User deserializedUser = mapper.readValue(userJson, User.class);
                User user = mapper.readValue(userJson, User.class);  // Deserialize with the correct type
                setCurrentUser(user);
            }
        }

        return response;
    }

    public Response signUp(String username, String password, LocalDate birthDate, String address, Gender gender, double balance) throws IOException { //create functionality
    	Request request = new Request("SIGNUP", Map.of("username", username, "password", password,"birthdate",birthDate,"address",address,"gender",gender,"balance",balance),getCurrentUser());
        return sendRequest(request);
    }

    public Response deleteUser(String username) throws IOException { //delete functionailty
        Request request = new Request("DELETE_USER", Map.of("username", username),getCurrentUser());
        return sendRequest(request);
    }
    
    public Response updateCustomer(String oldUsername,String newUsername, String password, LocalDate birthDate, String address, Gender gender, double balance) throws IOException { //update functionailty
    	Request request = new Request("UPDATE_USER", Map.of("oldUsername", oldUsername,"newUsername",newUsername, "password", password,"birthdate",birthDate,"address",address,"gender",gender,"balance",balance),getCurrentUser());
        return sendRequest(request);
    }
    
    
    //PRODUCT REQUESTS

    public Response addProduct(String categoryName, String name, String description, double price, LocalDate expirationDate) throws IOException {
    	Request request = new Request("ADD_PRODUCT", Map.of("categoryname", categoryName,"name",name,"description",description,"price",price,"expirationdate",expirationDate),getCurrentUser());
        return sendRequest(request);
    }

    public Response deleteProduct(String productName) throws IOException {
    	Request request = new Request("DELETE_PRODUCT", Map.of("name", productName),getCurrentUser());
        return sendRequest(request);
    }
    
    public Response viewProductsByCategory(String categoryName) throws IOException {
    	Request request = new Request("VIEW_PRODUCTS_BY_CATEGORY", Map.of("categoryname", categoryName),getCurrentUser());
        return sendRequest(request);
    }
    
    public Response viewAllProducts() throws IOException {
    	Request request = new Request("VIEW_ALL_PRODUCTS", Map.of(),getCurrentUser());
        return sendRequest(request);
    }
    
    public Response updateProduct(String oldName,String newName,String description,double price,LocalDate expirationdate) throws IOException {
    	Request request = new Request("UPDATE_PRODUCT", Map.of("oldname",oldName,"newname",newName,"description",description,"price",price,"expirationdate",expirationdate),getCurrentUser());
        return sendRequest(request);
    }
    
    //CATEGORY REQUESTS

    public Response getCategories() throws IOException {
    	Request request = new Request("GET_CATEGORIES", Map.of(),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response deleteCategory(String categoryName) throws IOException {
    	Request request = new Request("DELETE_CATEGORY", Map.of("categoryname",categoryName),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response addCategory(String name,String description,String id) throws IOException {
    	Request request = new Request("ADD_CATEGORY", Map.of("name",name,"description",description,"id",id),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response updateCategory(String Oldname,String newName,String description,String id) throws IOException {
    	Request request = new Request("UPDATE_CATEGORY", Map.of("oldname",Oldname,"newname",newName,"description",description,"id",id),getCurrentUser());
    	return sendRequest(request);
    }

    public Response getCart() throws IOException {
        Request request = new Request("GET_CART", Map.of(),getCurrentUser());
        return sendRequest(request);
    }
    
    public Response clearCart() throws IOException {
    	Request request = new Request("CLEAR_CART", Map.of(),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response addToCart(String productName,int quantity) throws IOException {
    	Request request = new Request("ADD_ITEM", Map.of("productname",productName,"quantity",quantity),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response removeItemFromCart(String productName) throws IOException {
    	Request request = new Request("REMOVE_ITEM", Map.of("productname",productName),getCurrentUser());
    	return sendRequest(request);
    }
    
    public Response requestPurchase(String username) throws IOException {
    	Request request = new Request("REQUEST_PURCHASE", Map.of(),getCurrentUser());
    	return sendRequest(request);
    }

    public ObjectMapper getMapper() {
        return this.mapper;
    }

}
