import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Product {

	private static final String UPC = "upc";
	private static final String PNAME = "pname";
	private static final String BRAND = "brand";
	private static final String QUANTITY = "package_quantity";
	
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public Product(ResultSet set) throws SQLException {
		this(set.getString(UPC), set.getString(PNAME), set.getString(BRAND), set.getInt(QUANTITY));
	}
	
	public Product(String upc, String pname, String brand, Integer packageQuantity) {
		if(upc != null) {
			attributes.put(UPC, upc);
		}
		if(pname != null) {
			attributes.put(PNAME, pname);
		}
		if(brand != null) {
			attributes.put(BRAND, brand);
		}
		if(packageQuantity != null) {
			attributes.put(QUANTITY, packageQuantity);
		}
	}
	
	public String getUpc() {
		return (String)attributes.get(UPC);
	}
	
	public String getPname() {
		return (String)attributes.get(PNAME);
	}
	
	public String getBrand() {
		return (String)attributes.get(BRAND);
	}
	
	public Integer getPackageQuantity() {
		return(Integer)attributes.get(QUANTITY);
	}
	
	public int getNumberOfAttributes() {
		return attributes.size();
	}
	
	public List<Product> find(Connection conn) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select * from products p where");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		for(int i = 0; i < attributeNames.length; i++) {
			sql.append(" p.`" + attributeNames[i] + "`= ?");
			if(i < attributeNames.length - 1) {
				sql.append(" and");
			}
		}
		sql.append(';');
		// query database and extract results
		List<Product> pList = new ArrayList<Product>();
		try {
			PreparedStatement findProduct = conn.prepareStatement(sql.toString());
			for(int i = 0; i < attributeNames.length; i++) {
				if(attributeNames[i].equals(QUANTITY)) {
					findProduct.setInt(i + 1, (Integer)attributes.get(attributeNames[i]));
				} else {
					findProduct.setString(i + 1, (String)attributes.get(attributeNames[i]));
				}
			}
			ResultSet result = findProduct.executeQuery();
			while(result.next()) {
				pList.add(new Product(result));
			}
		} catch(SQLException e) {
			System.err.println("Error getting product data");
			System.err.println(e.getMessage());
			pList = null;
		}
		return pList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int added = 0;
		if(attributes.get(UPC) != null) {
			builder.append(attributes.get(UPC));
			added++;
		}
		if(attributes.get(PNAME) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(PNAME));
			added++;
		}
		if(attributes.get(BRAND) != null) {
			if(added > 0) {
				builder.append(" ");
			}
			builder.append(attributes.get(BRAND));
			added++;
		}
		return builder.toString();
	}
	
}
