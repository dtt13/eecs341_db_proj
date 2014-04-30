import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.table.TableColumn;


public class BusinessApp extends JFrame implements ActionListener {
	
	private static final String[] CONSTRAINT_COLS = {"property", "constraint", "value 1", "value 2"};
	private static final String[] PRODUCT_COLS = {"upc", "pname", "value" };
	
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
		productsResults = createResultTable(CONSTRAINT_COLS);
		JScrollPane prScroll = new JScrollPane(productsResults);
		productsSelection = createComboBox();
		productFindButton = new JButton("Analyze");
		productFindButton.addActionListener(this);
//		productResetButton = new JButton("Reset");
//		productResetButton.addActionListener(this);
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
		
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(productFindButton)) {
			System.out.println("Finding...");
//		} else if(src.equals(storeSearchButton)) {
//			Store s = new Store(null, null, null, searchCityText.getText(), searchStateText.getText(), searchZipText.getText(),
//					null, null, null, null, null, null, null);
//			Connection conn = DatabaseUtils.openConnection();
//			storeList = s.find(conn);
//			updateTable(storeSearchTable, storeList, STORE_SEARCH_COLS);
//			DatabaseUtils.closeConnection(conn);
		}
	}
	
	private JTable createConstraintTable(String[] columnNames) {
		JTable table = new JTable(new String[20][columnNames.length], columnNames);// {
//			@Override
//			public boolean isCellEditable(int row, int column) {
//				return true;
//			}
//		};
//		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	
	private JComboBox createComboBox() {
		return new JComboBox();
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
		cb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					int row = 0; // TODO find the actual row
//					JComboBox propBox = (JComboBox)productsConstraints.getValueAt(row, 0);
					JComboBox consBox = (JComboBox)productsConstraints.getValueAt(row, 1);
//					System.out.println(propBox);
					System.out.println(consBox);
					consBox.removeAllItems();
					String[] consItems = null;
					switch((String)e.getSource()) {
					case "product":
						consItems = new String[]{"upc","pname","brand"};
						break;
					case "stores":
						consItems = new String[]{"sid","city","state","zip"};
						break;
					case "stock":
						consItems = new String[]{"price greater than", "price less than", "price between"};
						break;
					case "category":
						consItems = new String[]{"cat_name"};
						break;
					}
					if(consItems != null) {
						for(String item : consItems) {
							consBox.addItem(item);
						}
						consBox.setSelectedIndex(0);
					}
				}
			}
		});
		propColumn.setCellEditor(new DefaultCellEditor(cb));
		
		// setup the constraint column
		TableColumn consColumn = productsConstraints.getColumnModel().getColumn(1);
		cb = new JComboBox(new String[]{"None"});
		consColumn.setCellEditor(new DefaultCellEditor(cb));
		
		
	}
	
//	private static String capitalize(String s) {
//		StringBuilder builder = new StringBuilder();
//		if(s != null) {
//			boolean isFirstLetter = true;
//			for(int i = 0; i < s.length(); i++) {
//				char nextChar = s.charAt(i);
//				if(isFirstLetter) {
//					builder.append(Character.toUpperCase(nextChar));
//					isFirstLetter = false;
//				} else {
//					builder.append(nextChar);
//				}
//				if(nextChar == ' ') {
//					isFirstLetter = true;
//				}
//			}
//		}
//		return builder.toString();
//	}

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
