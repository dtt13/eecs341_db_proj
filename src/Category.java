import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Category extends Relation {
	private static final String CAT_ID = "cat_id";
	private static final String CAT_NAME = "cat_name";
	private static final String PARENT_ID = "parent_id";
	
	public Category(ResultSet set) throws SQLException {
		this(set.getString(CAT_ID), set.getString(CAT_NAME), set.getString(PARENT_ID));
	}
	
	public Category(String catId, String catName, String parentId) {
		if(catId != null && (catId = catId.trim()).length() > 0) {
			attributes.put(CAT_ID, catId);
		}
		if(catName != null && (catName = catName.trim()).length() > 0) {
			attributes.put(CAT_NAME, catName);
		}
		if(parentId != null && (parentId = parentId.trim()).length() > 0) {
			attributes.put(PARENT_ID, parentId);
		}
	}
	
	public String getCatId() {
		return (String)attributes.get(CAT_ID);
	}
	
	public String getCatName() {
		return (String)attributes.get(CAT_NAME);
	}
	
	public String getParentId() {
		return (String)attributes.get(PARENT_ID);
	}
	
	public static List<Relation> getTopLevelCategories(Connection conn) {
		List<Relation> cList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("select * from category c where c.`parent_id` is null;");
			while(result.next()) {
				cList.add(new Category(result));
			}
			return cList;
		} catch(SQLException e) {
			System.err.println("Could not get top-level categories");
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public List<Relation> getChildCategories(Connection conn) {
		List<Relation> cList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("select * from category c where c.`parent_id`='" + getCatId() + "';");
			while(result.next()) {
				cList.add(new Category(result));
			}
			return cList;
		} catch(SQLException e) {
			System.err.println("Could not get child categories");
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	public List<Relation> getProducts(Connection conn) {
		List<Relation> pList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery("select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity "
					+ "from `has_cat` h, products p where h.`cat_id`='" + getCatId() + "' and p.`upc`=h.`upc`;");
			while(result.next()) {
				pList.add(new Product(result));
			}
			return pList;
		} catch(SQLException e) {
			System.err.println("Could not get products from categories");
			System.err.println(e.getMessage());
		}
		return null;
	}
}
