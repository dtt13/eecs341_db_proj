import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Customer extends Relation {
	
	private static final String CID = "cid";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	private static final String PHONE = "phone";
	private static final String EMAIL = "email";
	private static final String STREET = "street";
	private static final String CITY = "city";
	private static final String STATE = "state";
	private static final String ZIP = "zip";
	
	public Customer(ResultSet set) throws SQLException {
		this(set.getString(CID), set.getString(FIRST_NAME), set.getString(LAST_NAME),
				set.getString(PHONE), set.getString(EMAIL), set.getString(STREET),
				set.getString(CITY), set.getString(STATE), set.getString(ZIP));
	}
	
	public Customer(String cid, String firstName, String lastName, String phone, String email,
			String street, String city, String state, String zip) {
		if(cid != null && (cid = cid.trim()).length() > 0) {
			attributes.put(CID, cid);
		}
		if(firstName != null && (firstName = firstName.trim()).length() > 0) {
			attributes.put(FIRST_NAME, firstName);
		}
		if(lastName != null && (lastName = lastName.trim()).length() > 0) {
			attributes.put(LAST_NAME, lastName);
		}
		if(phone != null && (phone = phone.trim()).length() > 0) {
			attributes.put(PHONE, deformatPhone(phone));
		}
		if(email != null && (email = email.trim()).length() > 0) {
			attributes.put(EMAIL, email);
		}
		if(street != null && (street = street.trim()).length() > 0) {
			attributes.put(STREET, street);
		}
		if(city != null && (city = city.trim()).length() > 0) {
			attributes.put(CITY, city);
		}
		if(state != null && (state = state.trim()).length() > 0) {
			attributes.put(STATE, state);
		}
		if(zip != null && (zip = zip.trim()).length() > 0) {
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
		return formatPhone((String)attributes.get(PHONE));
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
	
	public List<Relation> find(Connection conn) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select * from customers c ");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		if(attributeNames.length > 0) {
			sql.append("where");
			for(int i = 0; i < attributeNames.length; i++) {
				sql.append(" c.`" + attributeNames[i] + "`= ?");
				if(i < attributeNames.length - 1) {
					sql.append(" and");
				}
			}
		}
		sql.append(';');
		// query database and extract results
		List<Relation> cList = new ArrayList<Relation>();
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
	
	public void insert() { // TODO
		if(attributes.get(CID) != null) {
			update();
		} else {
			
		}
	}
	
	public void update() { // TODO
		
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
		if(attributes.get(STREET) != null) {
			if(added > 0) {
				builder.append("\n");
			}
			builder.append(attributes.get(STREET));
			added++;
		}
		if(attributes.get(CITY) != null) {
			if(added > 0) {
				builder.append("\n");
			}
			builder.append(attributes.get(CITY));
			if(attributes.get(STATE) != null) {
				builder.append(", " + attributes.get(STATE));
				if(attributes.get(ZIP) != null) {
					builder.append(" " + attributes.get(ZIP));
				}
			}
			added++;
		}
		if(attributes.get(PHONE) != null) {
			if(added > 0) {
				builder.append("\n");
			}
			builder.append(formatPhone((String)attributes.get(PHONE)));
			added++;
		}
		if(attributes.get(EMAIL) != null) {
			if(added > 0) {
				builder.append("\n");
			}
			builder.append(attributes.get(EMAIL));
			added++;
		}
		return builder.toString();
	}
}