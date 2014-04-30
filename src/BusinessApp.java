import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


public class BusinessApp extends JFrame implements ActionListener {
	
	private static final String[] CONSTRAINT_COLS = {"property", "constraint", "value 1", "value 2"};
	private static final String[] PRODUCT_COLS = {"upc", "pname", "value"};
	private static final String[] SUPPLY_COLS = {"sid", "vid", "upc", "order_date", "value"};
	
	// store logo and title
	private JPanel titlePanel;
	private ImageIcon logo;
	private JLabel titleLabel;
	
	//top-level pane
	private JTabbedPane topLevelPane;
	
	// products tab
	private JPanel productsPanel;
	private JTable productsConstraints;
	private JTable productsResults;
	private JComboBox productsSelection;
	private JButton productFindButton;
//	private JButton productResetButton;
	
	// supply tab
	private JPanel supplyPanel;
	private JTable supplyConstraints;
	private JTable supplyResults;
	private JComboBox supplySelection;
	private JButton supplyFindButton;
	
	public BusinessApp() {
		this.setLayout(new BorderLayout());
		GroupLayout gl;
		
		// store logo and title
		titlePanel = new JPanel(new BorderLayout());
		logo = new ImageIcon("logo.jpg");
		titleLabel = new JLabel(logo);
		titlePanel.add(titleLabel, BorderLayout.WEST);
		this.add(titlePanel, BorderLayout.NORTH);
		
		topLevelPane = new JTabbedPane();
		this.add(topLevelPane, BorderLayout.CENTER);
		
		// products tab
		productsPanel = new JPanel();
		gl = new GroupLayout(productsPanel);
		productsPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		productsConstraints = createConstraintTable(CONSTRAINT_COLS);
		setupProductColumns();
		JScrollPane pcScroll = new JScrollPane(productsConstraints);
		productsResults = createResultTable(PRODUCT_COLS);
		JScrollPane prScroll = new JScrollPane(productsResults);
		productsSelection = createProductsComboBox();
		productFindButton = new JButton("Analyze");
		productFindButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(pcScroll)
						.addComponent(productsSelection)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(productFindButton)
								)
						.addComponent(prScroll)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
								.addComponent(pcScroll)
								.addComponent(productsSelection)
								)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(productFindButton)
										)
								.addComponent(prScroll)
								)
						)
				);
		topLevelPane.addTab("Products", productsPanel);
		
		// supply tab
		supplyPanel = new JPanel();
		gl = new GroupLayout(supplyPanel);
		supplyPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		supplyConstraints = createConstraintTable(CONSTRAINT_COLS);
		setupSupplyColumns();
		JScrollPane scScroll = new JScrollPane(supplyConstraints);
		supplyResults = createResultTable(SUPPLY_COLS);
		JScrollPane srScroll = new JScrollPane(supplyResults);
		supplySelection = createSupplyComboBox();
		supplyFindButton = new JButton("Analyze");
		supplyFindButton.addActionListener(this);
//		productResetButton = new JButton("Reset");
//		productResetButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(scScroll)
						.addComponent(supplySelection)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(supplyFindButton)
								)
						.addComponent(srScroll)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
								.addComponent(scScroll)
								.addComponent(supplySelection)
								)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(supplyFindButton)
										)
								.addComponent(srScroll)
								)
						)
				);
		topLevelPane.addTab("Supply", supplyPanel);
		
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(productFindButton)) {
			analyzeProducts();
		} else if(src.equals(supplyFindButton)) {
			analyzeSupply();
		}
	}
	
	private JTable createConstraintTable(String[] columnNames) {
		JTable table = new JTable(new String[20][columnNames.length], columnNames);// {
		return table;
	}
	
	private JTable createResultTable(String[] columnNames) {
		JTable table = new JTable(new String[0][columnNames.length], columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}
	
	private void updateResultTable(JTable table, String[] data, String[] columnNames) {
		DefaultTableModel tm = (DefaultTableModel)table.getModel();
		tm.addRow(data);
	}
	
	private void clearResultTable(JTable table) {
		int numColumns = table.getColumnCount();
		String[] columnNames = new String[numColumns];
		for(int i = 0; i < numColumns; i++) {
			columnNames[i] = table.getColumnName(i);
		}
		String[][] tableData = new String[0][numColumns];
		table.setModel(new DefaultTableModel(tableData, columnNames));
	}
	
	private JComboBox createProductsComboBox() {
		String[] items = new String[]{"all results", "top selling"};
		return new JComboBox(items);
	}
	
	private JComboBox createSupplyComboBox() {
		String[] items = new String[]{"all results", "total number of supply orders", "max cost", "average delivery time"};
		return new JComboBox(items);
	}
	
	private void setupProductColumns() {
		JComboBox cb;
		// setup the property column
		TableColumn propColumn = productsConstraints.getColumnModel().getColumn(0);
		cb = new JComboBox();
		cb.addItem("products");
		cb.addItem("stores");
		cb.addItem("stock");
		cb.addItem("category");
		propColumn.setCellEditor(new DefaultCellEditor(cb));
	}

	private void setupSupplyColumns() {
		JComboBox cb;
		// setup the property column
		TableColumn propColumn = supplyConstraints.getColumnModel().getColumn(0);
		cb = new JComboBox();
		cb.addItem("vendors");
		cb.addItem("stores");
		cb.addItem("products");
		cb.addItem("supply");
		propColumn.setCellEditor(new DefaultCellEditor(cb));
	}
	
	private void analyzeProducts() {
		// generate the subquery
		StringBuilder subquery = new StringBuilder("select p.upc, p.pname, p.brand, p.`package_quantity` ");
		boolean isFirstStore = true;
		boolean isFirstStock = true;
		boolean isFirstCat = true;
		boolean isFirstWhere = true;
		StringBuilder fromClause = new StringBuilder("from products p");
		StringBuilder whereClause = new StringBuilder("where");
		TableModel tm = productsConstraints.getModel();
		for(int row = 0; row < productsConstraints.getRowCount(); row++) {
			if(tm.getValueAt(row,0) != null) {
				switch((String)tm.getValueAt(row, 0)) {
				case "products":
					switch((String)tm.getValueAt(row,1)) {
					case "upc":
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.upc='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "pname":
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.pname='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "brand":
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.brand='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				case "stores":
					if(isFirstStore) {
						fromClause.append(", stores s");
						if(isFirstStock) {
							fromClause.append(", stock t");
							isFirstStock = false;
							if(isFirstWhere) {
								isFirstWhere = false;
							} else {
								whereClause.append(" and");
							}
							whereClause.append(" p.upc=t.upc");
						}
						whereClause.append(" and t.sid=s.sid");
						isFirstStore = false;
					}
					switch((String)tm.getValueAt(row,1)) {
					case "sid":
						whereClause.append(" and s.sid='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "city":
						whereClause.append(" and s.city='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "state":
						whereClause.append(" and s.state='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "zip":
						whereClause.append(" and s.zip='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				case "stock":
					if(isFirstStock) {
						fromClause.append(", stock t");
						isFirstStock = false;
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.upc=t.upc");
					}
					switch((String)tm.getValueAt(row,1)) {
					case "price greater than":
						
						whereClause.append(" and t.price>" + (String)tm.getValueAt(row, 2));
						break;
					case "price less than":
						whereClause.append(" and t.price<" + (String)tm.getValueAt(row, 2));
						break;
					case "price between":
						whereClause.append(" and t.price between " + (String)tm.getValueAt(row, 2) + " and " + (String)tm.getValueAt(row, 3));
						break;
					}
					break;
				case "category":
					if(isFirstCat) {
						fromClause.append(", category c, `has_cat` h");
						isFirstCat = false;
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.upc=h.upc and h.`cat_id`=c.`cat_id`");
					}
					switch((String)tm.getValueAt(row,1)) {
					case "cat_name":
						whereClause.append(" and c.`cat_name`='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				}
			}
		}
		subquery.append(fromClause.toString() + " ");
		if(!isFirstWhere) {
			subquery.append(whereClause.toString());
		}
		System.out.println(subquery.toString());
		// outer query
		StringBuilder sql = new StringBuilder();
		boolean[] include = null;
		switch((String)productsSelection.getSelectedItem()) {
		case "all results":
			sql.append("select distinct(sub.upc), sub.pname from (" + subquery + ") as sub;");
			include = new boolean[]{true, true, false};
			break;
		case "top selling":
			sql.append("select distinct(sub.upc), sub.pname, sum(c.quantity) as total from (" + subquery + ") as sub, consists c where c.upc=sub.upc group by sub.upc, sub.pname order by total desc;"); 
			include = new boolean[]{true, true, true};
			break;
		}
		System.out.println(sql.toString());
		
		// execute query
		Connection conn = DatabaseUtils.openConnection();
		try {
			ResultSet result = conn.createStatement().executeQuery(sql.toString());
			clearResultTable(productsResults);
			while(result.next()) {
				String[] data = new String[include.length];
				int counter = 1;
				for(int i = 0; i < include.length; i++) {
					if(include[i]) {
						try {
							data[i] = result.getString(counter);
						} catch(Exception e) {
							try {
								data[i] = Integer.toString(result.getInt(counter));
							} catch(Exception e2) {
								data[i] = String.format("%10.2f", result.getDouble(counter));
							}
						} finally {
							counter++;
						}
					} else {
						data[i] = "";
					}
				}
				updateResultTable(productsResults, data, PRODUCT_COLS);
			}
		} catch (SQLException e) {
			System.err.println("Could not execute query");
			System.err.println(e.getMessage());
		}
		DatabaseUtils.closeConnection(conn);
	}
	
	private void analyzeSupply() {
		// generate the subquery
		StringBuilder subquery = new StringBuilder("select s.vid, s.sid, s.upc, s.cost, s.quantity, s.`order_date`, s.`recv_date` ");
		boolean isFirstStore = true;
		boolean isFirstVendor = true;
		boolean isFirstProduct = true;
		boolean isFirstWhere = true;
		StringBuilder fromClause = new StringBuilder("from supply s");
		StringBuilder whereClause = new StringBuilder("where");
		TableModel tm = supplyConstraints.getModel();
		for(int row = 0; row < supplyConstraints.getRowCount(); row++) {
			if(tm.getValueAt(row,0) != null) {
				switch((String)tm.getValueAt(row, 0)) {
				case "vendors":
					if(isFirstVendor) {
						fromClause.append(", vendors v");
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" v.vid=s.vid");
						isFirstVendor = false;
					}
					switch((String)tm.getValueAt(row,1)) {
					case "vname":
						whereClause.append(" and v.vname='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "vid":
						whereClause.append(" and v.vid='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "state":
						whereClause.append(" and v.state='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				case "stores":
					if(isFirstStore) {
						fromClause.append(", stores t");
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" s.sid=t.sid");
						isFirstStore = false;
					}
					switch((String)tm.getValueAt(row,1)) {
					case "sid":
						whereClause.append(" and t.sid='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "city":
						whereClause.append(" and t.city='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "state":
						whereClause.append(" and t.state='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "zip":
						whereClause.append(" and t.zip='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				case "product":
					if(isFirstProduct) {
						fromClause.append(", product p");
						isFirstProduct = false;
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" p.upc=s.upc");
					}
					switch((String)tm.getValueAt(row,1)) {
					case "upc":
						whereClause.append(" p.upc='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "pname":
						whereClause.append(" p.pname='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					case "brand":
						whereClause.append(" p.brand='" + (String)tm.getValueAt(row, 2) + "'");
						break;
					}
					break;
				case "supply":
					switch((String)tm.getValueAt(row,1)) {
					case "order date between":
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" s.`order_date` between '" + (String)tm.getValueAt(row, 2) + "' and '" + (String)tm.getValueAt(row, 3) + "'");
						break;
					case "receive date between":
						if(isFirstWhere) {
							isFirstWhere = false;
						} else {
							whereClause.append(" and");
						}
						whereClause.append(" s.`recv_date` between '" + (String)tm.getValueAt(row, 2) + "' and '" + (String)tm.getValueAt(row, 3) + "'");
						break;
					}
					break;
				}
			}
		}
		subquery.append(fromClause.toString() + " ");
		if(!isFirstWhere) {
			subquery.append(whereClause.toString());
		}
		// outer query
		StringBuilder sql = new StringBuilder();
		boolean[] include = null;
		switch((String)supplySelection.getSelectedItem()) {
		case "all results":
			sql.append("select sub.sid, sub.vid, sub.upc, sub.`order_date` from (" + subquery + ") as sub;"); 
			include = new boolean[]{true, true, true, true, false};
			break;
		case "total number of supply orders":
			sql.append("select count(*) from (" + subquery + ") as sub;");
			include = new boolean[]{false, false, false, false, true};
			break;
		case "max cost":
			sql.append("select sub.sid, sub.vid, sub.upc, sub.`order_date`, max(sub.cost) from (" + subquery + ") as sub;");
			include = new boolean[]{true, true, true, true, true};
			break;
		case "average delivery time":
			sql.append("select avg(datediff(sub.`recv_date`, sub.`order_date`)) from (" + subquery + ") as sub;");
			include = new boolean[]{false ,false, false, false, true};
			break; 
		}
		// execute query
		Connection conn = DatabaseUtils.openConnection();
		try {
			ResultSet result = conn.createStatement().executeQuery(sql.toString());
			clearResultTable(supplyResults);
			while(result.next()) {
				String[] data = new String[include.length];
				int counter = 1;
				for(int i = 0; i < include.length; i++) {
					if(include[i]) {
						try {
							data[i] = result.getString(counter);
						} catch(Exception e) {
							try {
								data[i] = Integer.toString(result.getInt(counter));
							} catch(Exception e2) {
								data[i] = String.format("%10.2f", result.getDouble(counter));
							}
						} finally {
							counter++;
						}
					} else {
						data[i] = "";
					}
				}
				updateResultTable(supplyResults, data, PRODUCT_COLS);
			}
		} catch (SQLException e) {
			System.err.println("Could not execute query");
			System.err.println(e.getMessage());
		}
		DatabaseUtils.closeConnection(conn);
	}
	
	public static void main(String[] args) {
		setLookAndFeel();
		BusinessApp b = new BusinessApp();
		b.setSize(1000, 700);
		b.setTitle("Business Analysis Tool");
		b.setDefaultCloseOperation(EXIT_ON_CLOSE);
		b.setVisible(true);
	}
	
	private static void setLookAndFeel() {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get (key);
			if (value != null && value instanceof FontUIResource){
				UIManager.put (key, new FontUIResource("Arial Black", Font.PLAIN, 16));
			}
		} 
	}
}
