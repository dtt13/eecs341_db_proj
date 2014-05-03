import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class OrderGenerator {

	public static void main(String[] args) {
		generateIncompleteVendorOrders();
	}
	
	private static void generateOrders() {
		Random random = new Random();
		List<Relation> stores = getAllStores();
		List<Relation> customers = getAllCustomers();
		List<Relation> products = getAllProducts();
		HashMap<Relation, Double> prices = getAllPrices(products);
		int counter = 0;
		for(Relation r : customers) { // for every customer
			Customer c = (Customer)r;
			int numberPurchases = random.nextInt(50) + 1;
			for(int i = 0; i < numberPurchases; i++) { // each person makes a random number of purchases
				List<Relation> consistsItems = new LinkedList<Relation>();
				int randomStore = 0;//random.nextInt(stores.size() - 1) + 1;
				int numberProducts = random.nextInt(6);
				for(int j = 0; j < numberProducts; j++) { // add products to shopping cart
					int randomQuantity = random.nextInt(4) + 1;
					int randomProduct = random.nextInt(products.size() - 1);
					consistsItems.add(new Consists(null, ((Product)products.get(randomProduct)).getUpc(), prices.get(products.get(randomProduct)), randomQuantity));
				}
				BusinessTransaction.checkoutItems((Store)stores.get(randomStore), c, consistsItems);
				System.out.println(++counter + " orders completed");
			}
		}
		System.out.println("all done.");
	}
	
	private static void generateCompletedVendorOrders() {
		Random random = new Random();
		List<Relation> products = getAllProducts();
		List<Relation> stores = getAllStores();
		HashMap<Relation, Double> prices = getAllPrices(products);
		Connection conn = DatabaseUtils.openConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch(SQLException e) {
			System.err.println("Failed to create a statement");
			return;
		}
		int counter = 0;
		for(Relation prodR : products) {
			Product p = (Product)prodR;
			for(Relation storeR : stores) {
				Store s = (Store)storeR;
				int vid = random.nextInt(16) + 1;
				int numOfOrders = random.nextInt(199) + 1;
				for(int count = 0; count < numOfOrders; count++) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					long unixTime = (946684800 + (long)((1398988800-946684800)*Math.random()))*1000;
					String orderDate = df.format(new Date(unixTime));
					unixTime += (172800 + (604800-172800)*Math.random()) * 1000;
					String recvDate = df.format(new Date(unixTime));
					int randomQuantity = (random.nextInt(9) + 1) * 10;
					Double cost = (double)((int)(randomQuantity * prices.get(prodR) * 0.90));
					try {
						String sql = "insert into supply(`vid`, `sid`, `upc`, `cost`, `quantity`, `order_date`, `recv_date`) values ('"
								+ vid + "', '" + s.getSid() + "', '" + p.getUpc() + "', " + cost + ", " + randomQuantity + ", '" + orderDate + "', '" + recvDate + "');";
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						System.err.println("Failed insert");
						System.err.println(e.getMessage());
					}
					System.out.println(++counter + " orders completed");
				}
			}
		}
		DatabaseUtils.closeConnection(conn);
	}
	
	private static void generateIncompleteVendorOrders() {
		Random random = new Random();
		List<Relation> products = getAllProducts();
		List<Relation> stores = getAllStores();
		HashMap<Relation, Double> prices = getAllPrices(products);
		Connection conn = DatabaseUtils.openConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch(SQLException e) {
			System.err.println("Failed to create a statement");
			return;
		}
		int counter = 0;
		for(Relation prodR : products) {
			Product p = (Product)prodR;
			for(Relation storeR : stores) {
				Store s = (Store)storeR;
				int vid = random.nextInt(16) + 1;
				int numOfOrders = random.nextInt(2) + 1;
				for(int count = 0; count < numOfOrders; count++) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					long unixTime = (1398643200 + (long)((1398988800-1398643200)*Math.random()))*1000;
					String orderDate = df.format(new Date(unixTime));
					int randomQuantity = (random.nextInt(9) + 1) * 10;
					Double cost = (double)((int)(randomQuantity * prices.get(prodR) * 0.90));
					try {
						String sql = "insert into supply(`vid`, `sid`, `upc`, `cost`, `quantity`, `order_date`) values ('"
								+ vid + "', '" + s.getSid() + "', '" + p.getUpc() + "', " + cost + ", " + randomQuantity + ", '" + orderDate + "');";
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						System.err.println("Failed insert");
						System.err.println(e.getMessage());
					}
					System.out.println(++counter + " orders completed");
				}
			}
		}
		DatabaseUtils.closeConnection(conn);
	}
	
	private static List<Relation> getAllCustomers() {
		Customer c = new Customer(null,null,null,null,null,null,null,null,null);
		Connection conn = DatabaseUtils.openConnection();
		List<Relation> customers = c.find(conn);
		DatabaseUtils.closeConnection(conn);
		return customers;
	}
	
	private static List<Relation> getAllStores() {
		Store s = new Store(null, null, null, null, null, null, null,null, null,null,null,null, null);
		Connection conn = DatabaseUtils.openConnection();
		List<Relation> stores = s.find(conn);
		DatabaseUtils.closeConnection(conn);
		return stores;
	}
	
	private static List<Relation> getAllProducts() {
		Product p = new Product(null, null, null, null);
		Connection conn = DatabaseUtils.openConnection();
		List<Relation> products = p.find(conn, new Store("1", null, null, null, null, null, null,null, null,null,null,null, null));
		DatabaseUtils.closeConnection(conn);
		return products;
	}
	
	public static HashMap<Relation, Double> getAllPrices(List<Relation> products) {
		HashMap<Relation, Double> mapping = new HashMap<>();
		Connection conn = DatabaseUtils.openConnection();
		for(Relation r : products) {
			Product p = (Product)r;
			Double price = p.findPrice(conn, new Store("1", null, null, null, null, null, null,null, null,null,null,null, null));
			mapping.put(r, price);
		}
		DatabaseUtils.closeConnection(conn);
		return mapping;
	}
}
