import java.sql.Connection;
import java.util.List;


public class Main {
	public static void main(String args[]) {
//		System.out.println(DatabaseUtils.getProductVersion());
//
//		// find customers
//		HashMap<String,String> attributes = new HashMap<String,String>();
//		attributes.put("first_name", "Candie");
//		BusinessTransaction.findCustomers(attributes);
		
		Customer c = new Customer(null, null, null, null, null, null, null, "Ohio", null);
//		System.out.println("number of attributes: " + c.getNumberOfAttributes());
		
		Connection conn = DatabaseUtils.openConnection();
		List<Customer> cList = c.find(conn);
		for(Customer cust : cList) {
			System.out.println(cust);
		}
		
		System.out.println();
		Product p = new Product(null, null, "Giro", null);
		List<Product> pList = p.find(conn);
		for(Product prod : pList) {
			System.out.println(prod);
		}
		
		System.out.println();
		Store s = new Store(null, null, null, null, "Ohio", null, null, null, null, null, null,null, null);
		List<Store> sList = s.find(conn);
		for(Store st : sList) {
			System.out.println(st);
		}
		
		DatabaseUtils.closeConnection(conn);
	}
}
