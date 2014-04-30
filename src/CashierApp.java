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
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;


public class CashierApp extends JFrame implements ActionListener {
	
	private static final String[] STORE_SELECTION_COLS = {"sid", "street", "city", "state", "phone"};
	private static final String[] ITEM_SEARCH_COLS = {"upc", "pname", "brand", "package_quantity"};
	private static final String[] CHECKOUT_COLS = {"upc", "quantity", "price"};
	private static final String[] CUSTOMER_SELECTION_COLS = {"first_name", "last_name", "phone", "email"};
	private static final String[] STOCK_CHECK_COLS = {"vid", "upc", "cost", "quantity", "order_date", "recv_date"};
	
	private static enum Screen {
		STORE_SIGNIN("Store Sign-In"),
		ITEM_SCAN("Item Scan"),
		STOCK_CHECK("Stock Check"),
		NEW_SUPPLY("New Supply Order"),
		CUSTOMER_SIGNIN("Customer Sign-In"),
		NEW_CUSTOMER("New Customer"),
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
	List<Relation> pendingSupplyList = null;
	List<Relation> recentSupplyList = null;
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
	private JButton checkStockButton;
	private JButton removeItemButton;
	private JButton checkoutButton;
	private JTable itemSearchTable;
	private JTable checkoutTable;
	private JLabel checkoutTotalLabel;
	
	// stock check screen
	private JPanel stockCheckPanel;
	private JLabel stockItemLabel;
	private JLabel stockAmountLabel;
	private JButton placeSupplyOrderButton;
	private JButton checkInSupplyButton;
	private JLabel pendingSupplyLabel;
	private JLabel recentSupplyLabel;
	private JTable pendingSupplyOrders;
	private JTable recentSupplyOrders;
	
	// new supply screen
	private JPanel newSupplyPanel;
	private JLabel newSupplyVidLabel;
	private JLabel newSupplyCostLabel;
	private JLabel newSupplyQuantityLabel;
	private JTextField newSupplyVidText;
	private JTextField newSupplyCostText;
	private JTextField newSupplyQuantityText;
	private JButton newSupplyConfirmButton;
	
	// customer sign-in screen
	private JPanel customerSignInPanel;
	private JLabel customerEmailLabel;
	private JLabel customerPhoneLabel;
	private JTextField customerEmailText;
	private JTextField customerPhoneText;
	private JTable customerSelectionTable;
	private JButton customerFindButton;
	private JButton customerNewButton;
	private JButton customerSelectionButton;
	
	// new customer screen
	private JPanel newCustomerPanel;
	private JLabel newCustFNLabel;
	private JLabel newCustLNLabel;
	private JLabel newCustPhoneLabel;
	private JLabel newCustEmailLabel;
	private JLabel newCustStreetLabel;
	private JLabel newCustCityLabel;
	private JLabel newCustStateLabel;
	private JLabel newCustZipLabel;
	private JTextField newCustFNText;
	private JTextField newCustLNText;
	private JTextField newCustPhoneText;
	private JTextField newCustEmailText;
	private JTextField newCustStreetText;
	private JTextField newCustCityText;
	private JTextField newCustStateText;
	private JTextField newCustZipText;
	private JButton newCustomerSelectionButton;
	
	// confirm order screen
	private JPanel confirmOrderPanel;
	private JLabel customerInfoLabel;
	private JLabel confirmOrderTotalLabel;
	private JTextArea customerInfoText;
	private JTable confirmOrderTable;
	private JButton confirmOrderButton;
	
	public CashierApp() {
		this.setLayout(new BorderLayout());
		GroupLayout gl;
		
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
		gl = new GroupLayout(storeSignInPanel);
		storeSignInPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
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
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(storeSidLabel)
								.addComponent(storeSidText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(storeCityLabel)
								.addComponent(storeCityText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(storeStateLabel)
								.addComponent(storeStateText)
								)		
						.addComponent(storeFindButton)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(storeScrollContainer)
						.addComponent(storeSelectionButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(storeSidLabel)
										.addComponent(storeSidText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(storeCityLabel)
										.addComponent(storeCityText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
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
		gl = new GroupLayout(itemScanPanel);
		itemScanPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
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
		checkStockButton = new JButton("Check Stock");
		checkStockButton.addActionListener(this);
		checkoutTable = createTable(CHECKOUT_COLS);
		JScrollPane checkoutScrollContainer = new JScrollPane(checkoutTable);
		checkoutTotalLabel = new JLabel();
		removeItemButton = new JButton("<<Remove");
		removeItemButton.addActionListener(this);
		checkoutButton = new JButton("Checkout");
		checkoutButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(upcLabel)
								.addComponent(itemSearchUpcText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(pnameLabel)
								.addComponent(itemSearchPnameText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(brandLabel)
								.addComponent(itemSearchBrandText)
								)		
						.addComponent(itemSearchButton)
						.addComponent(searchScrollContainer)
						.addGroup(gl.createSequentialGroup()
								.addComponent(checkStockButton)
								.addComponent(addItemButton)
								)	
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(checkoutScrollContainer)
						.addComponent(checkoutTotalLabel)
						.addComponent(removeItemButton)
						.addComponent(checkoutButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(upcLabel)
						.addComponent(itemSearchUpcText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(pnameLabel)
						.addComponent(itemSearchPnameText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(brandLabel)
						.addComponent(itemSearchBrandText)
						)
				.addComponent(itemSearchButton)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(checkoutScrollContainer)
						.addComponent(searchScrollContainer)
						)
				.addComponent(checkoutTotalLabel)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(checkStockButton)
						.addComponent(addItemButton)
						.addComponent(removeItemButton)
						)
				.addComponent(checkoutButton)
				);
		topLevelScreen.add(itemScanPanel, Screen.ITEM_SCAN.getName());
		
		// stock check screen
		stockCheckPanel = new JPanel();
		gl = new GroupLayout(stockCheckPanel);
		stockCheckPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		stockItemLabel = new JLabel();
		stockAmountLabel = new JLabel();
		placeSupplyOrderButton = new JButton("Place Order");
		placeSupplyOrderButton.addActionListener(this);
		checkInSupplyButton = new JButton("Check In Order");
		checkInSupplyButton.addActionListener(this);
		pendingSupplyLabel = new JLabel("Pending Supply Orders");
		recentSupplyLabel = new JLabel("Recent Supply Orders");
		pendingSupplyOrders = createTable(STOCK_CHECK_COLS);
		JScrollPane pendingContainer = new JScrollPane(pendingSupplyOrders);
		recentSupplyOrders = createTable(STOCK_CHECK_COLS);
		JScrollPane recentContainer = new JScrollPane(recentSupplyOrders);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(stockItemLabel)
						.addComponent(stockAmountLabel)
						.addComponent(recentSupplyLabel)
						.addGroup(gl.createSequentialGroup()
								.addComponent(recentContainer)
								)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(placeSupplyOrderButton)
								.addComponent(checkInSupplyButton)
								)
						.addComponent(pendingSupplyLabel)
						.addGroup(gl.createSequentialGroup()
								.addComponent(pendingContainer)
								)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addComponent(stockItemLabel)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(stockAmountLabel)
						.addComponent(placeSupplyOrderButton)
						.addComponent(checkInSupplyButton)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(recentSupplyLabel)
						.addComponent(pendingSupplyLabel)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(recentContainer)
						.addComponent(pendingContainer)
						)
				);
		topLevelScreen.add(stockCheckPanel, Screen.STOCK_CHECK.getName());
		
		// new supply screen
		newSupplyPanel = new JPanel();
		gl = new GroupLayout(newSupplyPanel);
		newSupplyPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		newSupplyVidLabel = new JLabel("Vendor ID");
		newSupplyCostLabel = new JLabel("Cost");
		newSupplyQuantityLabel = new JLabel("Quantity");
		newSupplyVidText = new JTextField();
		newSupplyCostText = new JTextField();
		newSupplyQuantityText = new JTextField();
		newSupplyConfirmButton = new JButton("Confirm");
		newSupplyConfirmButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(newSupplyVidLabel)
						.addComponent(newSupplyCostLabel)
						.addComponent(newSupplyQuantityLabel)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(newSupplyVidText)
						.addComponent(newSupplyCostText)
						.addComponent(newSupplyQuantityText)
						.addComponent(newSupplyConfirmButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newSupplyVidLabel)
						.addComponent(newSupplyVidText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newSupplyCostLabel)
						.addComponent(newSupplyCostText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newSupplyQuantityLabel)
						.addComponent(newSupplyQuantityText)
						)
				.addComponent(newSupplyConfirmButton)
				);
		topLevelScreen.add(newSupplyPanel, Screen.NEW_SUPPLY.getName());
		
		// customer sign-in screen
		customerSignInPanel = new JPanel();
		gl = new GroupLayout(customerSignInPanel);
		customerSignInPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		customerEmailLabel = new JLabel("Email");
		customerPhoneLabel = new JLabel("Phone");
		customerEmailText = new JTextField();
		customerPhoneText = new JTextField();
		customerFindButton = new JButton("Find");
		customerFindButton.addActionListener(this);
		customerNewButton = new JButton("New");
		customerNewButton.addActionListener(this);
		customerSelectionButton = new JButton("OK");
		customerSelectionButton.addActionListener(this);
		customerSelectionTable = createTable(CUSTOMER_SELECTION_COLS);
		JScrollPane customerScrollContainer = new JScrollPane(customerSelectionTable);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(customerEmailLabel)
								.addComponent(customerEmailText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(customerPhoneLabel)
								.addComponent(customerPhoneText)
								)	
						.addGroup(gl.createSequentialGroup()
								.addComponent(customerNewButton)
								.addComponent(customerFindButton)
								)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(customerScrollContainer)
						.addComponent(customerSelectionButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(customerEmailLabel)
										.addComponent(customerEmailText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(customerPhoneLabel)
										.addComponent(customerPhoneText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(customerNewButton)
										.addComponent(customerFindButton)
										)
								)
						.addComponent(customerScrollContainer)
						)
				.addComponent(customerSelectionButton)
				);
		topLevelScreen.add(customerSignInPanel, Screen.CUSTOMER_SIGNIN.getName());
		
		// new customer screen
		newCustomerPanel = new JPanel();
		gl = new GroupLayout(newCustomerPanel);
		newCustomerPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		newCustFNLabel = new JLabel("First Name");
		newCustLNLabel = new JLabel("Last Name");
		newCustPhoneLabel = new JLabel("Phone");
		newCustEmailLabel = new JLabel("Email");
		newCustStreetLabel = new JLabel("Street");
		newCustCityLabel = new JLabel("City");
		newCustStateLabel = new JLabel("State");
		newCustZipLabel = new JLabel("Zip");
		newCustFNText = new JTextField();
		newCustLNText = new JTextField();
		newCustPhoneText = new JTextField();
		newCustEmailText = new JTextField();
		newCustStreetText = new JTextField();
		newCustCityText = new JTextField();
		newCustStateText = new JTextField();
		newCustZipText = new JTextField();
		newCustomerSelectionButton = new JButton("OK");
		newCustomerSelectionButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(newCustFNLabel)
						.addComponent(newCustLNLabel)
						.addComponent(newCustPhoneLabel)
						.addComponent(newCustEmailLabel)
						.addComponent(newCustStreetLabel)
						.addComponent(newCustCityLabel)
						.addComponent(newCustStateLabel)
						.addComponent(newCustZipLabel)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(newCustFNText)
						.addComponent(newCustLNText)
						.addComponent(newCustPhoneText)
						.addComponent(newCustEmailText)
						.addComponent(newCustStreetText)
						.addComponent(newCustCityText)
						.addComponent(newCustStateText)
						.addComponent(newCustZipText)
						.addComponent(newCustomerSelectionButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustFNLabel)
						.addComponent(newCustFNText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustLNLabel)
						.addComponent(newCustLNText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustPhoneLabel)
						.addComponent(newCustPhoneText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustEmailLabel)
						.addComponent(newCustEmailText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustStreetLabel)
						.addComponent(newCustStreetText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustCityLabel)
						.addComponent(newCustCityText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustStateLabel)
						.addComponent(newCustStateText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(newCustZipLabel)
						.addComponent(newCustZipText)
						)
				.addComponent(newCustomerSelectionButton)
				);
		topLevelScreen.add(newCustomerPanel, Screen.NEW_CUSTOMER.getName());
		
		// confirm order screen
		confirmOrderPanel = new JPanel();
		gl = new GroupLayout(confirmOrderPanel);
		confirmOrderPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		customerInfoLabel = new JLabel("Customer Info");
		confirmOrderTotalLabel = new JLabel();
		updateTotal();
		customerInfoText = new JTextArea();
		customerInfoText.setEditable(false);
		confirmOrderTable = createTable(CHECKOUT_COLS);
		JScrollPane confirmScrollContainer = new JScrollPane(confirmOrderTable);
		confirmOrderButton = new JButton("Confirm");
		confirmOrderButton.addActionListener(this);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(customerInfoLabel)
						.addComponent(customerInfoText)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(confirmScrollContainer)
						.addComponent(confirmOrderTotalLabel)
						.addComponent(confirmOrderButton)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
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
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		resetNewCustomer();
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
		updateTotal();
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
		customerEmailText.setText("");
		customerPhoneText.setText("");
	}
	
	private void resetNewCustomer() {
		newCustFNText.setText("");
		newCustLNText.setText("");
		newCustPhoneText.setText("");
		newCustEmailText.setText("");
		newCustStreetText.setText("");
		newCustCityText.setText("");
		newCustStateText.setText("");
		newCustZipText.setText("");
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
		} else if(src.equals(checkStockButton)) {
			int productIndex = itemSearchTable.getSelectedRow();
			if(productIndex >= 0) {
				product = (Product)productList.get(productIndex);
				Connection conn = DatabaseUtils.openConnection();
				int stock = product.findStock(conn, store);
				pendingSupplyList = product.findPendingSupplyOrders(conn, store);
				recentSupplyList = product.findRecentSupplyOrders(conn, store);
				DatabaseUtils.closeConnection(conn);
				stockItemLabel.setText(product.getPname());
				stockAmountLabel.setText("Current Stock:  " + stock);
				updateTable(pendingSupplyOrders, pendingSupplyList, STOCK_CHECK_COLS);
				updateTable(recentSupplyOrders, recentSupplyList, STOCK_CHECK_COLS);
				switchScreen(Screen.STOCK_CHECK);
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
			if(!checkoutList.isEmpty()) {
				switchScreen(Screen.CUSTOMER_SIGNIN);
			}
		} else if(src.equals(placeSupplyOrderButton)) {
			int recentIndex = recentSupplyOrders.getSelectedRow();
			if(recentIndex >= 0) {
				Supply supply = (Supply)recentSupplyList.get(recentIndex);
				newSupplyVidText.setText(supply.getVid());
				newSupplyCostText.setText(supply.getCost().toString());
				newSupplyQuantityText.setText(supply.getQuantity().toString());
			} else {
				newSupplyVidText.setText("");
				newSupplyCostText.setText("");
				newSupplyQuantityText.setText("");
			}
			switchScreen(Screen.NEW_SUPPLY);
		} else if(src.equals(checkInSupplyButton)) {
			int pendingIndex = pendingSupplyOrders.getSelectedRow();
			if(pendingIndex >= 0) {
				Supply supply = (Supply)pendingSupplyList.get(pendingIndex);
				Connection conn = DatabaseUtils.openConnection();
				supply.checkInOrder(conn);
				int stock = product.findStock(conn, store);
				pendingSupplyList = product.findPendingSupplyOrders(conn, store);
				recentSupplyList = product.findRecentSupplyOrders(conn, store);
				DatabaseUtils.closeConnection(conn);
				stockAmountLabel.setText("Current Stock:  " + stock);
				updateTable(pendingSupplyOrders, pendingSupplyList, STOCK_CHECK_COLS);
				updateTable(recentSupplyOrders, recentSupplyList, STOCK_CHECK_COLS);
			}
		} else if(src.equals(newSupplyConfirmButton)) {
			if(!newSupplyVidText.getText().trim().isEmpty() && !newSupplyCostText.getText().trim().isEmpty()
					&& !newSupplyCostText.getText().trim().isEmpty()) {
				Supply supply;
				try{ 
					supply = new Supply(newSupplyVidText.getText(), store.getSid(), product.getUpc(),
							Double.valueOf(newSupplyCostText.getText()), Integer.valueOf(newSupplyQuantityText.getText()), null, null);
				} catch(NumberFormatException ne) {
					System.err.println(ne.getMessage());
					return;
				}
				Connection conn = DatabaseUtils.openConnection();
				supply.placeOrder(conn);
				pendingSupplyList = product.findPendingSupplyOrders(conn, store);
				DatabaseUtils.closeConnection(conn);
				updateTable(pendingSupplyOrders, pendingSupplyList, STOCK_CHECK_COLS);
				switchScreen(Screen.STOCK_CHECK);
			}
		} else if(src.equals(customerFindButton)) {
			Customer c = new Customer(null, null, null, customerPhoneText.getText(), customerEmailText.getText(),
					null, null, null, null);
			Connection conn = DatabaseUtils.openConnection();
			customerList = c.find(conn);
			DatabaseUtils.closeConnection(conn);
			updateTable(customerSelectionTable, customerList, CUSTOMER_SELECTION_COLS);
		} else if(src.equals(customerNewButton)) {
			switchScreen(Screen.NEW_CUSTOMER);
		} else if(src.equals(customerSelectionButton)) {
			int customerIndex = customerSelectionTable.getSelectedRow();
			if(customerIndex >= 0) {
				customer = (Customer)customerList.get(customerIndex);
				customerInfoText.setText(customer.toString());
				updateTable(confirmOrderTable, checkoutList, CHECKOUT_COLS);
				switchScreen(Screen.CONFIRM_ORDER);
			}
		} else if(src.equals(newCustomerSelectionButton)) {
			if(!(newCustFNText.getText().trim().isEmpty() && newCustLNText.getText().trim().isEmpty() && newCustPhoneText.getText().trim().isEmpty()
					&& newCustEmailText.getText().trim().isEmpty() && newCustStreetText.getText().trim().isEmpty() && newCustCityText.getText().trim().isEmpty()
					&& newCustStateText.getText().trim().isEmpty() && newCustZipText.getText().trim().isEmpty())) {
				customer = new Customer(null, newCustFNText.getText(), newCustLNText.getText(), newCustPhoneText.getText(),
						newCustEmailText.getText(), newCustStreetText.getText(), newCustCityText.getText(), newCustStateText.getText(),
						newCustZipText.getText());
				Connection conn = DatabaseUtils.openConnection();
				customer.insert(conn);
				DatabaseUtils.closeConnection(conn);
				customerEmailText.setText(newCustFNText.getText());
				customerPhoneText.setText(newCustLNText.getText());
				resetNewCustomer();
				customerInfoText.setText(customer.toString());
				updateTable(confirmOrderTable, checkoutList, CHECKOUT_COLS);
				switchScreen(Screen.CONFIRM_ORDER);
			}
		} else if(src.equals(confirmOrderButton)) {
			BusinessTransaction.checkoutItems(store, customer, checkoutList);
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
			case STOCK_CHECK:
			case CUSTOMER_SIGNIN:
				switchScreen(Screen.ITEM_SCAN);
				break;
			case NEW_SUPPLY:
				switchScreen(Screen.STOCK_CHECK);
				break;
			case NEW_CUSTOMER:
			case CONFIRM_ORDER:
				switchScreen(Screen.CUSTOMER_SIGNIN);
				break;
			default:
				;
			}
		}
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

	public static void main(String[] args) {
		setLookAndFeel();
		CashierApp c = new CashierApp();
		c.setSize(1000, 700);
		c.setTitle("Cashier Application");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.setVisible(true);
	}
}
