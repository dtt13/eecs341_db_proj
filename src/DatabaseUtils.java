import java.sql.*;

public class DatabaseUtils {
	
//	private static DatabaseUtils du = new DatabaseUtils();
	private static String driver = "com.mysql.jdbc.Driver";
	private static String server = "jdbc:mysql://localhost/sporting_goods";
	private static String userId = "dtt13";
	private static String pswd = "D8abases";
	
	/**
	 * Constructor sets up the driver for using MySQL.
	 */
	public DatabaseUtils() {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find the driver!");
		}
	}
//	
//	public static DatabaseUtils getUtils() {
//		return du;
//	}
	
	/**
	 * Determines the product name and version of the database.
	 * @return a String containing the data product name and version
	 */
	public static String getProductVersion() {
		Connection conn = openConnection();
		if(conn != null) {
			try {
//				System.out.println(conn.getAutoCommit());
				DatabaseMetaData meta = conn.getMetaData();
				return meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion();
			} catch (SQLException e) {
				System.err.println("Could not get database product name and version");
				e.printStackTrace();
			} finally {
				closeConnection(conn);
			}
		}
		return "";
	}
	
	/**
	 * Starts a database connection.
	 * @return a new database connection; null if connection fails
	 */
	public static Connection openConnection() {
		Connection conn = null;
		try {
//			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(server, userId, pswd);
//			System.out.println("Connection successful!");
			return conn;
		} catch (SQLException e) {
			System.err.println("Could not open a connection to the database");
			System.err.println(e.getMessage());
			closeConnection(conn);
		}
		return null;
	}
	
	/**
	 * Closes a database connection.
	 * @param conn connection to close
	 */
	public static void closeConnection(Connection conn) {
		try {
			conn.close();
//			System.out.println("Connection closed");
		} catch (SQLException e) {
			System.err.println("Could not close the database connection");
			System.err.println(e.getMessage());
		}
	}
}