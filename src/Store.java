import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Store {
	
	private static final String SID = "sid";
	private static final String PHONE = "phone";
	private static final String STREET = "street";
	private static final String CITY = "city";
	private static final String STATE = "state";
	private static final String ZIP = "zip";
	private static final String SUN = "sun_hours";
	private static final String MON = "mon_hours";
	private static final String TUES = "tues_hours";
	private static final String WED = "wed_hours";
	private static final String THUR = "thur_hours";
	private static final String FRI = "fri_hours";
	private static final String SAT = "sat_hours";
	
	HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public Store(ResultSet set) throws SQLException {
		this(set.getString(SID), set.getString(PHONE), set.getString(STREET),
				set.getString(CITY), set.getString(STATE), set.getString(ZIP),
				set.getString(SUN), set.getString(MON), set.getString(TUES),
				set.getString(WED), set.getString(THUR), set.getString(FRI),
				set.getString(SAT));
	}
	
	public Store(String sid, String phone, String street, String city, String state,
			String zip, String sunHours, String monHours, String tuesHours,
			String wedHours, String thurHours, String friHours, String satHours) {
		if(sid != null) {
			attributes.put(SID, sid);
		}
		if(phone != null) {
			attributes.put(PHONE, phone);
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
		if(sunHours != null) {
			attributes.put(SUN, sunHours);
		}
		if(monHours != null) {
			attributes.put(MON, monHours);
		}
		if(tuesHours != null) {
			attributes.put(TUES, tuesHours);
		}
		if(wedHours != null) {
			attributes.put(WED, wedHours);
		}
		if(thurHours != null) {
			attributes.put(THUR, thurHours);
		}
		if(friHours != null) {
			attributes.put(FRI, friHours);
		}
		if(satHours != null) {
			attributes.put(SAT, satHours);
		}
	}
	
	public String getSid() {
		return (String)attributes.get(SID);
	}
	
	public String getPhone() {
		return (String)attributes.get(PHONE);
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
	
	public String[] getHours() {
		return new String[]{
				(String)attributes.get(SUN),
				(String)attributes.get(MON),
				(String)attributes.get(TUES),
				(String)attributes.get(WED),
				(String)attributes.get(THUR),
				(String)attributes.get(FRI),
				(String)attributes.get(SAT)};
	}
	
	public int getNumberOfAttributes() {
		return attributes.size();
	}
	
	public List<Store> find(Connection conn) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select * from stores s where");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		for(int i = 0; i < attributeNames.length; i++) {
			sql.append(" s.`" + attributeNames[i] + "`= ?");
			if(i < attributeNames.length - 1) {
				sql.append(" and");
			}
		}
		sql.append(';');
		// query database and extract results
		List<Store> sList = new ArrayList<Store>();
		try {
			PreparedStatement findStore = conn.prepareStatement(sql.toString());
			for(int i = 0; i < attributeNames.length; i++) {
				findStore.setString(i + 1, (String)attributes.get(attributeNames[i]));
			}
			ResultSet result = findStore.executeQuery();
			while(result.next()) {
				sList.add(new Store(result));
			}
		} catch(SQLException e) {
			System.err.println("Error getting store data");
			System.err.println(e.getMessage());
			sList = null;
		}
		return sList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int added = 0;
		if(attributes.get(SID) != null) {
			builder.append(attributes.get(SID));
			added++;
		}
		if(attributes.get(CITY) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(CITY));
			added++;
		}
		if(attributes.get(STATE) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(STATE));
			added++;
		}
		if(attributes.get(ZIP) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(ZIP));
			added++;
		}
		
		return builder.toString();
	}
}
