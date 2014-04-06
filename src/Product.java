import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Product extends Relation {

	private static final String UPC = "upc";
	private static final String PNAME = "pname";
	private static final String BRAND = "brand";
	private static final String QUANTITY = "package_quantity";
	
//	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public Product(ResultSet set) throws SQLException {
		this(set.getString(UPC), set.getString(PNAME), set.getString(BRAND), set.getInt(QUANTITY));
	}
	
	public Product(String upc, String pname, String brand, Integer packageQuantity) {
		if(upc != null && (upc = upc.trim()).length() > 0) {
			attributes.put(UPC, upc);
		}
		if(pname != null && (pname = pname.trim()).length() > 0) {
			attributes.put(PNAME, pname);
		}
		if(brand != null && (brand = brand.trim()).length() > 0) {
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
	
	public List<Relation> find(Connection conn, Store store) {
		// generate sql expression
		StringBuilder sql  = new StringBuilder("select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity` "
				+ "from products p, stock s where s.`sid`=" + store.getSid() + " and s.`upc`=p.`upc`");
		String attributeNames[] = attributes.keySet().toArray(new String[]{});
		for(int i = 0; i < attributeNames.length; i++) {
			sql.append(" and p.`" + attributeNames[i] + "`= ?");
		}
		sql.append(';');
		// query database and extract results
		List<Relation> pList = new ArrayList<Relation>();
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
	
	public Double findPrice(Connection conn, Store store) {
		try {
			Statement findPrice = conn.createStatement();
			ResultSet result = findPrice.executeQuery("select s.`price` "
					+ "from stock s where s.`upc`='" + getUpc() + "' and s.`sid`='" + store.getSid() + "';");
			while(result.next()) {
				return result.getDouble(1);
			}
		} catch(SQLException e) {
			System.err.println("Error getting price");
			System.err.println(e.getMessage());
		}
		return null;
	}
	
//	public static String[][] createTableData(List<Product> products, String[] columnNames) {
//		String[][] output = new String[products.size()][columnNames.length];
//		for(int productNum = 0; productNum < output.length; productNum++) {
//			for(int columnNum = 0; columnNum < output[productNum].length; columnNum++) {
//				String result = products.get(productNum).attributes.get(columnNames[columnNum]).toString();
//				if(result != null) {
//					output[productNum][columnNum] = result;
//				} else {
//					output[productNum][columnNum] = "";
//				}
//			}
//		}
//		return output;
//	}
	
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
