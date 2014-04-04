import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;


public class Cashier extends JFrame implements ActionListener {
	
	private static enum Screen{
		STORE_SIGNIN("Store Sign-In"),
		STORE_SELECTION("Store Selection"),
		ITEM_SCAN("Item Scan");
		private String name;
		private Screen(String name) { this.name = name; }		
		public String getName() { return name; }
	};
	
	// global variables for storing query results
	List<Store> storeList = null;
	List<Product> productList = null;
	List<Consists> checkoutList = new LinkedList<Consists>();
	List<Customer> customerList = null;
	
	Store store = null;
	Product product = null;
	Customer customer = null;
	
	// top level panel and logo and title
	private JPanel topLevelScreen;
	private JPanel titlePanel;
	private ImageIcon logo;
	private JLabel titleLabel;
	
	// store sign-in screen
	private JPanel storeSignInPanel;
//	private JLabel storeSignInLabel;
	private JTextField sidSignInText;
	private JButton sidSignInButton;
	
	// store selection screen
	private JPanel storeSelectionPanel;
	private JTable storeSelectionTable;
	private JButton storeSelectionButton;
	
	// item scan screen
	private JPanel itemScanPanel;
	private JPanel itemSearchPanel;
	private JTextField itemSearchUpcText;
	private JTextField itemSearchBrandText;
	private JButton itemSearchButton;
	private JButton addItemButton;
	private JTable itemSearchTable;
	private JPanel checkoutPanel;
	private JButton removeItemButton;
	private JButton checkoutButton;
	private JTable checkoutTable;
	private JLabel checkoutTotalLabel;
	
	
	public Cashier() {
		this.setLayout(new BorderLayout());
		
		// top level panel
		topLevelScreen = new JPanel(new CardLayout());
		this.add(topLevelScreen, BorderLayout.CENTER);
		
		// store logo and title
		titlePanel = new JPanel(new BorderLayout());
		logo = new ImageIcon("logo.jpg");
		titleLabel = new JLabel(logo);
		titlePanel.add(titleLabel, BorderLayout.WEST);
		this.add(titlePanel, BorderLayout.NORTH);
		
		// store sign-in screen
		storeSignInPanel = new JPanel(new FlowLayout());
		sidSignInText = new JTextField("Enter sid", 20);
		sidSignInButton = new JButton("Find");
		sidSignInButton.addActionListener(this);
		storeSignInPanel.add(sidSignInText);
		storeSignInPanel.add(sidSignInButton);
		topLevelScreen.add(storeSignInPanel, Screen.STORE_SIGNIN.getName());
		
		// store selection screen
		storeSelectionPanel = new JPanel(new FlowLayout());
		storeSelectionTable = createTable();
		storeSelectionButton = new JButton("OK");
		storeSelectionButton.addActionListener(this);
		storeSelectionPanel.add(storeSelectionTable);
		storeSelectionPanel.add(storeSelectionButton);
		topLevelScreen.add(storeSelectionPanel, Screen.STORE_SELECTION.getName());
		
		// item scan screen
		itemScanPanel = new JPanel(new BorderLayout());
		
		itemSearchPanel = new JPanel(new FlowLayout());
		itemSearchUpcText = new JTextField("Enter UPC", 12);
		itemSearchBrandText = new JTextField("Enter Brand", 20);
		itemSearchButton = new JButton("Find");
		itemSearchButton.addActionListener(this);
		itemSearchTable = createTable();
		addItemButton = new JButton("Add>>");
		addItemButton.addActionListener(this);
		itemSearchPanel.add(itemSearchUpcText);
		itemSearchPanel.add(itemSearchBrandText);
		itemSearchPanel.add(itemSearchButton);
		itemSearchPanel.add(itemSearchTable);
		itemSearchPanel.add(addItemButton);
		checkoutPanel = new JPanel(new FlowLayout());
		checkoutTable = createTable();
		checkoutTotalLabel = new JLabel("$0.00");
		removeItemButton = new JButton("<<Remove");
		removeItemButton.addActionListener(this);
		checkoutButton = new JButton("Checkout");
		checkoutButton.addActionListener(this);
		checkoutPanel.add(checkoutTable);
		checkoutPanel.add(checkoutTotalLabel);
		checkoutPanel.add(removeItemButton);
		checkoutPanel.add(checkoutButton);
		
		itemScanPanel.add(itemSearchPanel, BorderLayout.CENTER);
		itemScanPanel.add(checkoutPanel, BorderLayout.EAST);
		topLevelScreen.add(itemScanPanel, Screen.ITEM_SCAN.getName());
		
		switchScreen(Screen.STORE_SIGNIN);
	}
	
	private void switchScreen(Screen screen) {
		CardLayout layout = (CardLayout)topLevelScreen.getLayout();
		layout.show(topLevelScreen, screen.getName());
		titleLabel.setText(screen.getName());
	}
	
	private JTable createTable() {
		JTable table = new JTable() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return table;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(sidSignInButton)) {
			Store s = new Store(null/*sidSignInText.getText()*/	, null, null, null,
					"Ohio", null, null, null, null, null, null, null, null); //TODO change that shit back
			Connection conn = DatabaseUtils.openConnection();
			storeList = s.find(conn);
			String[] columns = {"sid", "street", "city", "state", "phone"};
			String[][] data = Store.createTableData(storeList, columns);
			storeSelectionTable.setModel(new DefaultTableModel(data, columns));
//			storeSelectionTable.getCol
			DatabaseUtils.closeConnection(conn);
			switchScreen(Screen.STORE_SELECTION);
		} else if(src.equals(storeSelectionButton)) {
			int storeIndex = storeSelectionTable.getSelectedRow();
			if(storeIndex >= 0) {
				store = storeList.get(storeIndex);
				System.out.println(store);
				switchScreen(Screen.ITEM_SCAN);
			} else {
				// TODO display message to gui
			}
		} else if(src.equals(itemSearchButton)) {
			Product p = new Product(itemSearchUpcText.getText(), null,
					itemSearchBrandText.getText(), null);
			Connection conn = DatabaseUtils.openConnection();
			productList = p.find(conn, store);
			String[] columns = {"upc", "pname", "brand", "package_quantity"};
			String[][] data = Product.createTableData(productList, columns);
			itemSearchTable.setModel(new DefaultTableModel(data, columns));
			DatabaseUtils.closeConnection(conn);
		} else if(src.equals(addItemButton)) {
			int productIndex = itemSearchTable.getSelectedRow();
			if(productIndex >= 0) {
				product = productList.get(productIndex);
				Connection conn = DatabaseUtils.openConnection();
				Consists consists = new Consists(product, product.getPrice(conn, store));
				if(checkoutList.contains(consists)) {
					checkoutList.get(checkoutList.indexOf(consists)).incrementQuantity();
				} else {
					checkoutList.add(new Consists(product, product.getPrice(conn, store)));
				}
				DatabaseUtils.closeConnection(conn);
				System.out.println(product + " added to checkout list");
//				for(Consists checkout : checkoutList) {
//					System.out.println(checkout);
//				}
				//TODO update the checkout table
			}
		} else if(src.equals(removeItemButton)) {
			int checkoutIndex = checkoutTable.getSelectedRow();
			if(checkoutIndex >= 0) {
				Consists checkout = checkoutList.get(checkoutIndex);
				checkout.decrementQuantity();
				if(checkout.getQuantity() == 0) {
					checkoutList.remove(checkoutIndex);
				}
				//TODO update the checkout table
				System.out.println(checkout + " removed from checkout list");
			}
		} else if(src.equals(checkoutButton)) {
			//TODO complete transaction
		}
	}

	public static void main(String[] args) {
		setLookAndFeel();
		Cashier c = new Cashier();
		c.setSize(1000, 700);
		c.setTitle("Cashier Application");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.setVisible(true);
	}
	
	public static void setLookAndFeel() {
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
