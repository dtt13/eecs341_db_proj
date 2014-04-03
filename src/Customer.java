import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Customer {
	
	private static final String CID = "cid";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	private static final String PHONE = "phone";
	private static final String EMAIL = "email";
	private static final String STREET = "street";
	private static final String CITY = "city";
	private static final String STATE = "state";
	private static final String ZIP = "zip";
	
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public Customer(ResultSet set) throws SQLException {
		this(set.getString(CID), set.getString(FIRST_NAME), set.getString(LAST_NAME),
				set.getString(PHONE), set.getString(EMAIL), set.getString(STREET),
				set.getString(CITY), set.getString(STATE), set.getString(ZIP));
	}
	
	public Customer(String cid, String firstName, String lastName, String phone, String email,
			String street, String city, String state, String zip) {
		if(cid != null) {
			attributes.put(CID, cid);
		}
		if(firstName != null) {
			attributes.put(FIRST_NAME, firstName);
		}
		if(lastName != null) {
			attributes.put(LAST_NAME, lastName);
		}
		if(phone != null) {
			attributes.put(PHONE, phone);
		}
		if(email != null) {
			attributes.put(EMAIL, email);
		}
		if(street != null) {
			attributes.put(STREET, street);
		}
		if(city != null) {
			attributes.put(CITY, city);
		}
		if(state != null) {
			attributes.put(STATE, state);
		}
		if(zip != null) {
			attributes.put(ZIP, zip);
		}
	}
		
	public String getCid() {
		return (String)attributes.get(CID);
	}
	
	public String getFirstName() {
		return (String)attributes.get(FIRST_NAME);
	}
	
	public String getLastName() {
		return (String)attributes.get(LAST_NAME);
	}
	
	public String getPhone() {
		return (String)attributes.get(PHONE);
	}
	
	public String getEmail() {
		return (String)attributes.get(EMAIL);
	}
	
	public String getStreet() {
		return (String)attributes.get(STREET);
	}
	
	public String getCity() {
		return (String)attributes.get(CITY);
	}
	
	public String getState() {
		return (String)attributes.get(STATE);
	}
	
	public String getZip() {
		return (String)attributes.get(ZIP);
	}
	
	public int getNumberOfAttributes() {
		return attributes.size();
	}
	
	public List<Customer> find(Connection conn) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select * from customers c where");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		for(int i = 0; i < attributeNames.length; i++) {
			sql.append(" c.`" + attributeNames[i] + "`= ?");
			if(i < attributeNames.length - 1) {
				sql.append(" and");
			}
		}
		sql.append(';');
		// query database and extract results
		List<Customer> cList = new ArrayList<Customer>();
		try {
			PreparedStatement findCustomer = conn.prepareStatement(sql.toString());
			for(int i = 0; i < attributeNames.length; i++) {
				findCustomer.setString(i + 1, (String)attributes.get(attributeNames[i]));
			}
			ResultSet result = findCustomer.executeQuery();
			while(result.next()) {
				cList.add(new Customer(result));
			}
		} catch(SQLException e) {
			System.err.println("Error getting customer data");
			System.err.println(e.getMessage());
			cList = null;
		}
		return cList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int added = 0;
		if(attributes.get(FIRST_NAME)!= null) {
			builder.append(attributes.get(FIRST_NAME));
			added++;
		}
		if(attributes.get(LAST_NAME) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(LAST_NAME));
			added++;
		}		
		return builder.toString();
	}
	
//	private static String formatPhone(String phoneNum) {
//		StringBuilder builder = new StringBuilder();
//		int index = 0;
//		switch(phoneNum.length()) {
//		case 11:
//			builder.append(phoneNum.charAt(index++));
//			builder.append(" ");
//		case 10:
//			builder.append(phoneNum.substring(index, index + 3));
//			index += 3;
//			builder.append("-");
//		default:
//			builder.append(phoneNum.substring(index, index + 3) + "-" + phoneNum.substring(index + 3, index + 7));
//		}
//		return builder.toString();
//	}
}