import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Supply extends Relation {
	private static final String VID = "vid";
	private static final String SID = "sid";
	private static final String UPC = "upc";
	private static final String COST = "cost";
	private static final String QUANTITY = "quantity";
	private static final String ORDER_DATE = "order_date";
	private static final String RECV_DATE = "recv_date";

	public Supply(ResultSet set) throws SQLException {
		this(set.getString(VID), set.getString(SID), set.getString(UPC), set.getDouble(COST),
				set.getInt(QUANTITY), set.getString(ORDER_DATE), set.getString(RECV_DATE));
	}
	
	public Supply(String vid, String sid, String upc, Double cost,
			Integer quantity, String orderDate, String recvDate) {
		if(vid != null && (vid = vid.trim()).length() > 0) {
			attributes.put(VID, vid);
		}
		if(sid != null && (sid = sid.trim()).length() > 0) {
			attributes.put(SID, sid);
		}
		if(upc != null && (upc = upc.trim()).length() > 0) {
			attributes.put(UPC, upc);
		}
		if(cost != null) {
			attributes.put(COST, cost);
		}
		if(quantity != null) {
			attributes.put(QUANTITY, quantity);
		}
		if(orderDate != null && (orderDate = orderDate.trim()).length() > 0) {
			attributes.put(ORDER_DATE, orderDate);
		}
		if(recvDate != null && (recvDate = recvDate.trim()).length() > 0) {
			attributes.put(RECV_DATE, recvDate);
		}
	}
	
	public String getVid() {
		return (String)attributes.get(VID);
	}
	
	public String getSid() {
		return (String)attributes.get(SID);
	}
	
	public String getUpc() {
		return (String)attributes.get(UPC);
	}
	
	public Double getCost() {
		return (Double)attributes.get(COST);
	}
	
	public Integer getQuantity() {
		return (Integer)attributes.get(QUANTITY);
	}
	
	public String getOrderDate() {
		return (String)attributes.get(ORDER_DATE);
	}
	
	public String getReceivedDate() {
		return (String)attributes.get(RECV_DATE);
	}
	
	public void placeOrder(Connection conn) {
		if(getOrderDate() == null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			attributes.put(ORDER_DATE, df.format(Calendar.getInstance().getTime()));
			try {
				Statement findSupply = conn.createStatement();
				findSupply.executeUpdate("insert into supply(`vid`, `sid`, `upc`, `cost`, `quantity`, `order_date`) values ('"
						+ getVid() + "', '" + getSid() + "', '" + getUpc() + "', " + getCost() + ", " + getQuantity() + ", '" + getOrderDate() + "');");
				System.out.println("successfully placed supply order");
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			}
		}
	}
	
	public void checkInOrder(Connection conn) {
		if(getReceivedDate() == null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			attributes.put(RECV_DATE, df.format(Calendar.getInstance().getTime()));
			try {
				conn.setAutoCommit(false);
				Statement findSupply = conn.createStatement();
				findSupply.executeUpdate("update supply set `recv_date`='" + getReceivedDate() + "' where vid='"
						+ getVid() + "' and sid='" + getSid() + "' and upc='" + getUpc() + "' and `order_date`='" + getOrderDate() + "';");
				findSupply.executeUpdate("update stock set `quantity`=`quantity` + " + getQuantity() + " where sid='"
						+ getSid() + "' and upc='" + getUpc() + "';");
				conn.commit();
				System.out.println("successfully checked in supply order");
			} catch(SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch(SQLException e) {
					System.err.println("Could not turn on auto commit");
				}
			}
		}
	}
	
}
