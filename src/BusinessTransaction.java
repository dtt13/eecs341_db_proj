import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;


public class BusinessTransaction {
	
	public static void checkoutItems(Store store, Customer customer, List<Relation> consistsItems) {
		if(consistsItems.isEmpty()) {
			return;
		}
		
		// Open a database connection and commit the updates
		Connection conn = DatabaseUtils.openConnection();
		try {
			conn.setAutoCommit(false);
			int orderNo = -1;
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(generateOrdersUpdate(consistsItems));
			ResultSet result = stmt.executeQuery("select last_insert_id();");
			if(result.next()) {
				orderNo = result.getInt(1);
			} else {
				throw new SQLException("Could not auto increment");
			}
			stmt.executeUpdate(generateConsistsUpdate(orderNo, consistsItems));
			stmt.executeUpdate(generatePurchaseUpdate(orderNo, store, customer));
			// TODO also subtract from inventory
			conn.commit();
			System.out.println("successfully commited order");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			DatabaseUtils.closeConnection(conn);
		}
	}
	
	
	private static String generateOrdersUpdate(List<Relation> consistsItems) {
		return "insert into orders(`order_amount`) value (" + calculateTotalAmount(consistsItems) + ");";
	}

	private static String generateConsistsUpdate(int orderNo, List<Relation> consistsItems) {
		StringBuilder builder = new StringBuilder("insert into consists(`order_no`, `upc`, `price`, `quantity`) values ");
		ListIterator<Relation> it = consistsItems.listIterator();
		while(it.hasNext()) {
			Consists c = (Consists)it.next();
			builder.append("(" + orderNo + ", '" + c.getUpc() + "', " + c.getPrice() + ", " + c.getQuantity()+ ")");
			if(it.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(";");
		return builder.toString();
	}
	
	private static String generatePurchaseUpdate(int orderNo, Store store, Customer customer) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return "insert into purchase(`cid`, `sid`, `order_no`, `date`) values ('" + 
					customer.getCid() + "', '" + store.getSid() + "', " + orderNo + 
					", '" + df.format(Calendar.getInstance().getTime()) + "');";
	}
	
	
	private static double calculateTotalAmount(List<Relation> consistsItems) {
		double total = 0.0;
		for(Relation r : consistsItems) {
			Consists c = (Consists)r;
			total += Math.round(c.getQuantity() * c.getPrice() * 100.0) / 100.0;
		}
		return total;
	}
}
