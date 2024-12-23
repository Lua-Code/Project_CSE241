package Classes;
import java.util.ArrayList;

public class Category implements Displayable {
	private String name,Description,ID;
	private ArrayList<Product> products = new ArrayList<Product>();

	public Category() {}
	public Category(String name,String Description,String ID) {
		this.name = name;
		this.Description = Description;
		this.ID = ID;
	}
	public Category(String name) {
		this.name = name;
		this.Description = "";
		this.ID = "";
	}

	public ArrayList<Product> getProducts() {
		return this.products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	
	public void addProduct(Product product) {
		this.products.add(product);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}
	
    public Product findProduct(String input) {
    	for(Product p : getProducts()) {
    		if(p.getName().toLowerCase().equals(input.toLowerCase()) ) {
    			return p;
    		}
    	}
    	return null;
    }
    
	public void deleteProduct(Product p) {
		getProducts().remove(p);
	}
	
	public void display() {
		int x = 1;
    	System.out.println("---------------------------------");
		System.out.println("Category: "+this.getName());
    	System.out.println("---------------------------------");
		for(Product p : this.getProducts()) {
			System.out.println("Product No."+x+++":");
			p.display();
		}
	}

}
