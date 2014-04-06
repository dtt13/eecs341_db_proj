import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;


public class Cashier extends JFrame implements ActionListener {
	
	private static final String[] STORE_SELECTION_COLS = {"sid", "street", "city", "state", "phone"};
	private static final String[] ITEM_SEARCH_COLS = {"upc", "pname", "brand", "package_quantity"};
	private static final String[] CHECKOUT_COLS = {"upc", "quantity", "price"};
	private static final String[] CUSTOMER_SELECTION_COLS = {"first_name", "last_name", "phone", "email"};
	
	private static enum Screen{
		STORE_SIGNIN("Store Sign-In"),
		ITEM_SCAN("Item Scan"),
		CUSTOMER_SIGNIN("Customer Sign-In"),
		CONFIRM_ORDER("Confirm Order");
		private String name;
		private Screen(String name) { this.name = name; }		
		public String getName() { return name; }
	};
	
	private Screen currentScreen;
	
	// global variables for storing query results
	List<Relation> storeList = null;
	List<Relation> productList = null;
	List<Relation> customerList = null;
	List<Relation> checkoutList = new LinkedList<Relation>();
	
	Store store = null;
	Product product = null;
	Customer customer = null;
	
	// top level panel and logo and title
	private JPanel topLevelScreen;
	private JPanel titlePanel;
	private ImageIcon logo;
	private JLabel titleLabel;
	private JButton signOutButton;
	private JButton backButton;
	
	// store sign-in screen
	private JPanel storeSignInPanel;
	private JLabel storeSidLabel;
	private JLabel storeCityLabel;
	private JLabel storeStateLabel;
	private JTextField storeSidText;
	private JTextField storeCityText;
	private JTextField storeStateText;
	private JTable storeSelectionTable;
	private JButton storeFindButton;
	private JButton storeSelectionButton;
	
	// item scan screen
	private JPanel itemScanPanel;
	private JLabel upcLabel;
	private JLabel pnameLabel;
	private JLabel brandLabel;
	private JTextField itemSearchUpcText;
	private JTextField itemSearchPnameText;
	private JTextField itemSearchBrandText;
	private JButton itemSearchButton;
	private JButton addItemButton;
	private JTable itemSearchTable;
	private JButton removeItemButton;
	private JButton checkoutButton;
	private JTable checkoutTable;
	private JLabel checkoutTotalLabel;
	
	// customer sign-in screen
	private JPanel customerSignInPanel;
	private JLabel customerFNLabel;
	private JLabel customerLNLabel;
	private JTextField customerFNText;
	private JTextField customerLNText;
	private JTable customerSelectionTable;
	private JButton customerFindButton;
	private JButton customerSelectionButton;
	
	// confirm order screen
	private JPanel confirmOrderPanel;
	private JLabel customerInfoLabel;
	private JLabel confirmOrderTotalLabel;
	private JTextArea customerInfoText;
	private JTable confirmOrderTable;
	private JButton confirmOrderButton;
	
	public Cashier() {
		this.setLayout(new BorderLayout());
		GroupLayout groupLayout;
		
		// top level panel
		topLevelScreen = new JPanel(new CardLayout());
		this.add(topLevelScreen, BorderLayout.CENTER);
		
		// store logo and title
		titlePanel = new JPanel(new BorderLayout());
		logo = new ImageIcon("logo.jpg");
		titleLabel = new JLabel(logo);
		signOutButton = new JButton("Sign out");
		signOutButton.addActionListener(this);
		backButton = new JButton("Back");
		backButton.addActionListener(this);
		JPanel signOutPanel = new JPanel();
		signOutPanel.add(backButton);
		signOutPanel.add(signOutButton);
		titlePanel.add(titleLabel, BorderLayout.WEST);
		titlePanel.add(signOutPanel, BorderLayout.EAST);
		this.add(titlePanel, BorderLayout.NORTH);
		
		// store sign-in screen
		storeSignInPanel = new JPanel();
		groupLayout = new GroupLayout(storeSignInPanel);
		storeSignInPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		storeSidLabel = new JLabel("Store ID");
		storeCityLabel = new JLabel("City");
		storeStateLabel = new JLabel("State");
		storeSidText = new JTextField();
		storeCityText = new JTextField();
		storeStateText = new JTextField();
		storeFindButton = new JButton("Find");
		storeFindButton.addActionListener(this);
		storeSelectionButton = new JButton("OK");
		storeSelectionButton.addActionListener(this);
		storeSelectionTable = createTable(STORE_SELECTION_COLS);
		JScrollPane storeScrollContainer = new JScrollPane(storeSelectionTable);
		groupLayout.setHorizontalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(storeSidLabel)
								.addComponent(storeSidText)
								)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(storeCityLabel)
								.addComponent(storeCityText)
								)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(storeStateLabel)
								.addComponent(storeStateText)
								)		
						.addComponent(storeFindButton)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(storeScrollContainer)
						.addComponent(storeSelectionButton)
						)
				);
		groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(storeSidLabel)
										.addComponent(storeSidText)
										)
								.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(storeCityLabel)
										.addComponent(storeCityText)
										)
								.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(storeStateLabel)
										.addComponent(storeStateText)
										)
								.addComponent(storeFindButton)
								)
						.addComponent(storeScrollContainer)
						)
				.addComponent(storeSelectionButton)
				);
		topLevelScreen.add(storeSignInPanel, Screen.STORE_SIGNIN.getName());
		
		// item scan screen
		itemScanPanel = new JPanel(new BorderLayout());
		groupLayout = new GroupLayout(itemScanPanel);
		itemScanPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		upcLabel = new JLabel("UPC");
		pnameLabel = new JLabel("Name");
		brandLabel = new JLabel("Brand");
		itemSearchUpcText = new JTextField();
		itemSearchPnameText = new JTextField();
		itemSearchBrandText = new JTextField();
		itemSearchButton = new JButton("Find");
		itemSearchButton.addActionListener(this);
		itemSearchTable = createTable(ITEM_SEARCH_COLS);
		JScrollPane searchScrollContainer = new JScrollPane(itemSearchTable);
		addItemButton = new JButton("Add>>");
		addItemButton.addActionListener(this);
		checkoutTable = createTable(CHECKOUT_COLS);
		JScrollPane checkoutScrollContainer = new JScrollPane(checkoutTable);
		checkoutTotalLabel = new JLabel("$  0.00");
		removeItemButton = new JButton("<<Remove");
		removeItemButton.addActionListener(this);
		checkoutButton = new JButton("Checkout");
		checkoutButton.addActionListener(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(upcLabel)
								.addComponent(itemSearchUpcText)
								)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(pnameLabel)
								.addComponent(itemSearchPnameText)
								)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(brandLabel)
								.addComponent(itemSearchBrandText)
								)		
						.addComponent(itemSearchButton)
						.addComponent(searchScrollContainer)
						.addComponent(addItemButton)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(checkoutScrollContainer)
						.addComponent(checkoutTotalLabel)
						.addComponent(removeItemButton)
						.addComponent(checkoutButton)
						)
				);
		groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(upcLabel)
						.addComponent(itemSearchUpcText)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(pnameLabel)
						.addComponent(itemSearchPnameText)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(brandLabel)
						.addComponent(itemSearchBrandText)
						)
				.addComponent(itemSearchButton)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(checkoutScrollContainer)
						.addComponent(searchScrollContainer)
						)
				.addComponent(checkoutTotalLabel)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(addItemButton)
						.addComponent(removeItemButton)
						)
				.addComponent(checkoutButton)
				);
		topLevelScreen.add(itemScanPanel, Screen.ITEM_SCAN.getName());
		
		// customer sign-in screen
		customerSignInPanel = new JPanel();
		groupLayout = new GroupLayout(customerSignInPanel);
		customerSignInPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		customerFNLabel = new JLabel("First Name");
		customerLNLabel = new JLabel("Last Name");
		customerFNText = new JTextField();
		customerLNText = new JTextField();
		customerFindButton = new JButton("Find");
		customerFindButton.addActionListener(this);
		customerSelectionButton = new JButton("OK");
		customerSelectionButton.addActionListener(this);
		customerSelectionTable = createTable(CUSTOMER_SELECTION_COLS);
		JScrollPane customerScrollContainer = new JScrollPane(customerSelectionTable);
		groupLayout.setHorizontalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(customerFNLabel)
								.addComponent(customerFNText)
								)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(customerLNLabel)
								.addComponent(customerLNText)
								)	
						.addComponent(customerFindButton)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(customerScrollContainer)
						.addComponent(customerSelectionButton)
						)
				);
		groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(customerFNLabel)
										.addComponent(customerFNText)
										)
								.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(customerLNLabel)
										.addComponent(customerLNText)
										)
								.addComponent(customerFindButton)
								)
						.addComponent(customerScrollContainer)
						)
				.addComponent(customerSelectionButton)
				);
		topLevelScreen.add(customerSignInPanel, Screen.CUSTOMER_SIGNIN.getName());
		
		// confirm order screen
		confirmOrderPanel = new JPanel();
		groupLayout = new GroupLayout(confirmOrderPanel);
		confirmOrderPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		customerInfoLabel = new JLabel("Customer Info");
		confirmOrderTotalLabel = new JLabel(" $  0.00");
		customerInfoText = new JTextArea();
		customerInfoText.setEditable(false);
		confirmOrderTable = createTable(CHECKOUT_COLS);
		JScrollPane confirmScrollContainer = new JScrollPane(confirmOrderTable);
		confirmOrderButton = new JButton("Confirm");
		confirmOrderButton.addActionListener(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(customerInfoLabel)
						.addComponent(customerInfoText)
						)
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(confirmScrollContainer)
						.addComponent(confirmOrderTotalLabel)
						.addComponent(confirmOrderButton)
						)
				);
		groupLayout.setVerticalGroup(
				groupLayout.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(customerInfoLabel)
								.addComponent(customerInfoText)
								)
						.addComponent(confirmScrollContainer)
						)
				.addComponent(confirmOrderTotalLabel)
				.addComponent(confirmOrderButton)
				);
		topLevelScreen.add(confirmOrderPanel, Screen.CONFIRM_ORDER.getName());
							
		switchScreen(Screen.STORE_SIGNIN);
	}
	
	private void switchScreen(Screen screen) {
		CardLayout layout = (CardLayout)topLevelScreen.getLayout();
		layout.show(topLevelScreen, screen.getName());
		titleLabel.setText(screen.getName());
		switch(screen) {
		case STORE_SIGNIN:
			signOutButton.setEnabled(false);
			backButton.setEnabled(false);
			break;
		case ITEM_SCAN:
			signOutButton.setEnabled(true);
			backButton.setEnabled(false);
			break;
		default:
			signOutButton.setEnabled(true);
			backButton.setEnabled(true);
		}
		currentScreen = screen;
	}
	
	private JTable createTable(String[] columnNames) {
		JTable table = new JTable(new String[0][columnNames.length], columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		return table;
	}
	
	private void updateTable(JTable table, List<Relation> data, String[] columnNames) {
		String[][] tableData = Relation.createTableData(data, columnNames);
		table.setModel(new DefaultTableModel(tableData, columnNames));
	}
	
	private void clearTable(JTable table) {
		int numColumns = table.getColumnCount();
		String[] columnNames = new String[numColumns];
		for(int i = 0; i < numColumns; i++) {
			columnNames[i] = table.getColumnName(i);
		}
		String[][] tableData = new String[0][numColumns];
		table.setModel(new DefaultTableModel(tableData, columnNames));
	}
	
	private void updateTotal() {
		double total = 0.0;
		for(Relation r : checkoutList) {
			Consists c = (Consists)r;
			total += Math.round(c.getQuantity() * c.getPrice() * 100.0) / 100.0;
		}
		String result = "$" + String.format("%10.2f", total);
		checkoutTotalLabel.setText(result);
		confirmOrderTotalLabel.setText(result);
	}
	
	private void signOut() {
		resetStoreSignIn();
		resetItemScan();
		resetCustomerSignIn();
		switchScreen(Screen.STORE_SIGNIN);
	}
	
	private void resetStoreSignIn() {
		store = null;
		storeList = null;
		clearTable(storeSelectionTable);
		storeSidText.setText("");
		storeCityText.setText("");
		storeStateText.setText("");
	}
	
	private void resetItemScan() {
		product = null;
		productList = null;
		checkoutList = new LinkedList<Relation>();
		clearTable(itemSearchTable);
		clearTable(checkoutTable);
		itemSearchUpcText.setText("");
		itemSearchPnameText.setText("");
		itemSearchBrandText.setText("");
	}
	
	private void resetCustomerSignIn() {
		customer = null;
		customerList = null;
		clearTable(customerSelectionTable);
		customerFNText.setText("");
		customerLNText.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(storeFindButton)) {
			Store s = new Store(storeSidText.getText(), null, null, storeCityText.getText(),
					storeStateText.getText(), null, null, null, null, null, null, null, null);
			Connection conn = DatabaseUtils.openConnection();
			storeList = s.find(conn);
			DatabaseUtils.closeConnection(conn);
			updateTable(storeSelectionTable, storeList, STORE_SELECTION_COLS);
		} else if(src.equals(storeSelectionButton)) {
			int storeIndex = storeSelectionTable.getSelectedRow();
			if(storeIndex >= 0) {
				store = (Store)storeList.get(storeIndex);
				switchScreen(Screen.ITEM_SCAN);
			} else {
				// TODO display message to gui
			}
		} else if(src.equals(itemSearchButton)) {
			Product p = new Product(itemSearchUpcText.getText(), null,
					itemSearchBrandText.getText(), null);
			Connection conn = DatabaseUtils.openConnection();
			productList = p.find(conn, store);
			updateTable(itemSearchTable, productList, ITEM_SEARCH_COLS);
			DatabaseUtils.closeConnection(conn);
		} else if(src.equals(addItemButton)) {
			int productIndex = itemSearchTable.getSelectedRow();
			if(productIndex >= 0) {
				product = (Product)productList.get(productIndex);
				Connection conn = DatabaseUtils.openConnection();
				Consists consists = new Consists(product, null);
				if(checkoutList.contains(consists)) {
					((Consists)checkoutList.get(checkoutList.indexOf(consists))).incrementQuantity();
				} else {
					checkoutList.add(new Consists(product, product.findPrice(conn, store)));
				}
				DatabaseUtils.closeConnection(conn);
				updateTable(checkoutTable, checkoutList, CHECKOUT_COLS);
				updateTotal();
			}
		} else if(src.equals(removeItemButton)) {
			int checkoutIndex = checkoutTable.getSelectedRow();
			if(checkoutIndex >= 0) {
				Consists checkout = (Consists)checkoutList.get(checkoutIndex);
				checkout.decrementQuantity();
				if(checkout.getQuantity() == 0) {
					checkoutList.remove(checkoutIndex);
				}
				updateTable(checkoutTable, checkoutList, CHECKOUT_COLS);
				if(checkoutList.size() > checkoutIndex) {
					checkoutTable.setRowSelectionInterval(checkoutIndex, checkoutIndex);
				}
				updateTotal();
			}
		} else if(src.equals(checkoutButton)) {
			switchScreen(Screen.CUSTOMER_SIGNIN);
		} else if(src.equals(customerFindButton)) {
			Customer c = new Customer(null, customerFNText.getText(), customerLNText.getText(),
					null, null, null, null, null, null);
			Connection conn = DatabaseUtils.openConnection();
			customerList = c.find(conn);
			DatabaseUtils.closeConnection(conn);
			updateTable(customerSelectionTable, customerList, CUSTOMER_SELECTION_COLS);
		} else if(src.equals(customerSelectionButton)) {
			int customerIndex = customerSelectionTable.getSelectedRow();
			if(customerIndex >= 0) {
				customer = (Customer)customerList.get(customerIndex);
				customerInfoText.setText(customer.toString());
				updateTable(confirmOrderTable, checkoutList, CHECKOUT_COLS);
				switchScreen(Screen.CONFIRM_ORDER);
			} else {
				// TODO display message to gui
			}
		} else if(src.equals(confirmOrderButton)) {
			//TODO complete transaction
			resetItemScan();
			resetCustomerSignIn();
			switchScreen(Screen.ITEM_SCAN);
		} else if(src.equals(signOutButton)) {
			boolean isConfirmed = (JOptionPane.showConfirmDialog(this,
					"Any incomplete transactions will be lost upon signing out.\n"
					+ "Do you wish to continue?",
					"Sign out",
					JOptionPane.YES_NO_OPTION)) == 0;
			if(isConfirmed) {
				signOut();
			}
		} else if(src.equals(backButton)) {
			switch(currentScreen) {
			case CUSTOMER_SIGNIN:
				switchScreen(Screen.ITEM_SCAN);
				break;
			case CONFIRM_ORDER:
				switchScreen(Screen.CUSTOMER_SIGNIN);
				break;
			default:
				;
			}
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
