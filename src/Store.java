import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Store extends Relation {
	
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
		if(sid != null && (sid = sid.trim()).length() > 0) {
			attributes.put(SID, sid);
		}
		if(phone != null && (phone = phone.trim()).length() > 0) {
			attributes.put(PHONE, deformatPhone(phone));
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
		if(sunHours != null && (sunHours = sunHours.trim()).length() > 0) {
			attributes.put(SUN, sunHours);
		}
		if(monHours != null && (monHours = monHours.trim()).length() > 0) {
			attributes.put(MON, monHours);
		}
		if(tuesHours != null && (tuesHours = tuesHours.trim()).length() > 0) {
			attributes.put(TUES, tuesHours);
		}
		if(wedHours != null && (wedHours = wedHours.trim()).length() > 0) {
			attributes.put(WED, wedHours);
		}
		if(thurHours != null && (thurHours = thurHours.trim()).length() > 0) {
			attributes.put(THUR, thurHours);
		}
		if(friHours != null && (friHours = friHours.trim()).length() > 0) {
			attributes.put(FRI, friHours);
		}
		if(satHours != null && (satHours = satHours.trim()).length() > 0) {
			attributes.put(SAT, satHours);
		}
	}
	
	public String getSid() {
		return (String)attributes.get(SID);
	}
	
	public String getPhone() {
		return formatPhone((String)attributes.get(PHONE));
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
	
	public List<Relation> find(Connection conn) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select * from stores s ");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		if(attributeNames.length > 0) {
			sql.append("where");
			for(int i = 0; i < attributeNames.length; i++) {
				sql.append(" s.`" + attributeNames[i] + "`= ?");
				if(i < attributeNames.length - 1) {
					sql.append(" and");
				}
			}
		}
		sql.append(';');
		// query database and extract results
		List<Relation> sList = new ArrayList<Relation>();
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
	
	public String getStoreInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append(getStreet() + "\n");
		builder.append(getCity() + ", ");
		builder.append(getState() + " ");
		builder.append(getZip() + "\n");
		if(attributes.get(PHONE) != null) {
			builder.append(getPhone() + "\n");
		}
		String[] hours = getHours();
		builder.append("Sunday:\t" + formatHours(hours[0]) + "\n");
		builder.append("Monday:\t" + formatHours(hours[1]) + "\n");
		builder.append("Tuesday:\t" + formatHours(hours[2]) + "\n");
		builder.append("Wednesday:\t" + formatHours(hours[3]) + "\n");
		builder.append("Thursday:\t" + formatHours(hours[4]) + "\n");
		builder.append("Friday:\t" + formatHours(hours[5]) + "\n");
		builder.append("Saturday:\t" + formatHours(hours[6]));
		return builder.toString();
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
	
	private String formatHours(String hours) {
		if(hours != null) {
			String[] times = hours.split("-");
			for(int i = 0; i < times.length; i++) {
				int timeVal = Integer.parseInt(times[i]);
				
				if(timeVal < 1300) {
					times[i] = Integer.toString(timeVal / 100) + ":" + times[i].substring(2) + "AM";
				} else {
					times[i] = Integer.toString((timeVal / 100) - 12) + ":" + times[i].substring(2) + "PM";
				}
			}
			return times[0] + " - " + times[1];
		}
		return "Closed";
	}
	
}
