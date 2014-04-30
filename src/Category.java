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
	
	public List<Relation> getProducts(Connection conn, Store store) {
		List<Relation> pList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result;
			if(getParentId() == null) { // top-level
				result = stmt.executeQuery("select distinct(p.`upc`), p.`pname`, p.`brand`, p.`package_quantity`, t.`price`"
						+ "from category c, `has_cat` h, products p, stock t where c.`parent_id`='" + getCatId()
						+ "' and c.`cat_id`=h.`cat_id` and p.`upc`=h.`upc` and p.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "';");
			} else { // subcategory
				result = stmt.executeQuery("select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`, t.`price` "
						+ "from `has_cat` h, products p, stock t where h.`cat_id`='" + getCatId() + "' and p.`upc`=h.`upc` "
						+ "and p.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "';");
			}
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
	
	public List<Relation> getProductsByPrice(Connection conn, Store store) {
		List<Relation> pList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result;
			if(getParentId() == null) { // top-level
				result = stmt.executeQuery("select distinct(p.`upc`), p.`pname`, p.`brand`, p.`package_quantity`, t.`price`"
						+ "from category c, `has_cat` h, products p, stock t where c.`parent_id`='" + getCatId()
						+ "' and c.`cat_id`=h.`cat_id` and p.`upc`=h.`upc` and p.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "' order by t.`price` asc;");
			} else { // subcategory
				result = stmt.executeQuery("select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`, t.`price` "
						+ "from `has_cat` h, products p, stock t where h.`cat_id`='" + getCatId() + "' and p.`upc`=h.`upc` "
						+ "and p.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "' order by t.`price` asc;");
			}
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
	
	public List<Relation> getProductsByPopularity(Connection conn, Store store) {
		List<Relation> pList = new ArrayList<Relation>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet result;
			if(getParentId() == null) { // top-level
//				result = stmt.executeQuery("select distinct(p.`upc`), p.`pname`, p.`brand`, p.`package_quantity`, t.`price`"
//						+ "from category c, `has_cat` h, products p, stock t where c.`parent_id`='" + getCatId()
//						+ "' and c.`cat_id`=h.`cat_id` and p.`upc`=h.`upc` and p.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "';");
				result = stmt.executeQuery("select distinct(pop.`upc`), pop.`pname`, pop.`brand`, pop.`package_quantity`, t.`price` "
						+ "from (select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`, sum(c.`quantity`) as popularity from products p, consists c, purchase r "
						+ "where r.`sid`='" + store.getSid() + "' and r.`order_no`=c.`order_no` and c.`upc`=p.`upc` group by p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`) as pop, "
						+ "category c, `has_cat` h, stock t where c.`parent_id`='" + getCatId() + "' and c.`cat_id`=h.`cat_id` and pop.`upc`=h.`upc` and pop.`upc`=t.`upc` and t.`sid`='"+ store.getSid()
						+ "' order by pop.`popularity` desc;");
				//TODO fix
			} else { // subcategory
				result = stmt.executeQuery("select pop.`upc`, pop.`pname`, pop.`brand`, pop.`package_quantity`, t.`price` "
						+ "from (select p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`, sum(c.`quantity`) as popularity from products p, consists c, purchase r "
						+ "where r.`sid`='" + store.getSid() + "' and r.`order_no`=c.`order_no` and c.`upc`=p.`upc` group by p.`upc`, p.`pname`, p.`brand`, p.`package_quantity`) as pop, "
						+ "`has_cat` h, stock t where h.`cat_id`='" + getCatId() + "' and pop.`upc`=h.`upc` and pop.`upc`=t.`upc` and t.`sid`='"+ store.getSid() + "' order by pop.`popularity` desc;");
			}
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
	
	@Override
	public String toString() {
		return getCatName();
	}
}
