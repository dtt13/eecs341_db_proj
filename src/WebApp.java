import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

public class WebApp extends JFrame implements ActionListener {
	private static final String[] ITEM_SEARCH_COLS = {"pname", "brand", "package_quantity", "price"};
	private static final String[] STORE_SEARCH_COLS = {"street", "city", "state", "zip"};
	
	private static final Store webStore = new Store("1", null, null, null, null, null, null, null, null, null, null, null, null); // TODO make real web store entry
	
	private static enum Screen {
		ITEM_SEARCH("Item Search"),
		STORE_SEARCH("Store Finder");
		private String name;
		private Screen(String name) { this.name = name; }		
		public String getName() { return name; }
	};
	
	private Category lastTouchedCategory = null;
	private String lastComboSelection;
	
	private List<Relation> productList = null;
	private List<Relation> storeList = null;
	
	// store logo and title
	private JPanel titlePanel;
	private ImageIcon logo;
	private JLabel titleLabel;
	private JButton itemLookupButton;
	private JButton storeFinderButton;
	
	// top-level panel
	private JPanel topLevelScreen;
	
	// item search screen
	private JPanel itemSearchPanel;
	private JLabel searchBrandLabel;
	private JTextField searchBrandText;
	private JComboBox orderByBox;
	private JButton itemSearchButton;
	private JMenuBar categoryMenu;
	private JTable itemSearchTable;
	
	// store search screen
	private JPanel storeSearchPanel;
	private JLabel searchCityLabel;
	private JLabel searchStateLabel;
	private JLabel searchZipLabel;
	private JTextField searchCityText;
	private JTextField searchStateText;
	private JTextField searchZipText;
	private JButton storeSearchButton;
	private JTable storeSearchTable;
	private JTextArea storeInfoArea;
	
	public WebApp() {
		this.setLayout(new BorderLayout());
		GroupLayout gl;
		
		// store logo and title
		titlePanel = new JPanel(new BorderLayout());
		logo = new ImageIcon("logo.jpg");
		titleLabel = new JLabel(logo);
		itemLookupButton = new JButton("Home");
		itemLookupButton.addActionListener(this);
		storeFinderButton = new JButton("Store Locator");
		storeFinderButton.addActionListener(this);
		JPanel signOutPanel = new JPanel();
		signOutPanel.add(itemLookupButton);
		signOutPanel.add(storeFinderButton);
		titlePanel.add(titleLabel, BorderLayout.WEST);
		titlePanel.add(signOutPanel, BorderLayout.EAST);
		this.add(titlePanel, BorderLayout.NORTH);
		
		// top-level panel
		topLevelScreen = new JPanel(new CardLayout());
		this.add(topLevelScreen, BorderLayout.CENTER);
		
		// item search screen
		itemSearchPanel = new JPanel();
		gl = new GroupLayout(itemSearchPanel);
		itemSearchPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		searchBrandLabel = new JLabel("Brand");
		searchBrandText = new JTextField();
		itemSearchButton = new JButton("Search");
		itemSearchButton.addActionListener(this);
		categoryMenu = createMenuBar();
		orderByBox = createComboBox();
		JScrollPane menuScrollPane = new JScrollPane(categoryMenu);
		itemSearchTable = createTable(ITEM_SEARCH_COLS);
		JScrollPane itemSearchScrollPane = new JScrollPane(itemSearchTable);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addComponent(menuScrollPane)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(itemSearchScrollPane)
						.addGroup(gl.createSequentialGroup()
								.addComponent(searchBrandLabel)
								.addComponent(searchBrandText)
								.addComponent(itemSearchButton)
								.addComponent(orderByBox)
								)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(menuScrollPane)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(searchBrandLabel)
									.addComponent(searchBrandText)
									.addComponent(itemSearchButton)
									.addComponent(orderByBox)
									)
								.addComponent(itemSearchScrollPane)
								)
						)
				);
		topLevelScreen.add(itemSearchPanel, Screen.ITEM_SEARCH.getName());
		
		// store search screen
		storeSearchPanel = new JPanel();
		gl = new GroupLayout(storeSearchPanel);
		storeSearchPanel.setLayout(gl);
		gl.setAutoCreateGaps(true);
		gl.setAutoCreateContainerGaps(true);
		searchCityLabel = new JLabel("City");
		searchStateLabel = new JLabel("State");
		searchZipLabel = new JLabel("Zip code");
		searchCityText = new JTextField();
		searchStateText = new JTextField();
		searchZipText = new JTextField();
		storeSearchButton = new JButton("Find");
		storeSearchButton.addActionListener(this);
		storeSearchTable = createTable(STORE_SEARCH_COLS);
		storeSearchTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int storeIndex = storeSearchTable.getSelectedRow();
				if(storeIndex >= 0) {
					storeInfoArea.setText(((Store)storeList.get(storeIndex)).getStoreInfo());
					System.out.println(storeInfoArea.getSize());
				}
			}
		});
		JScrollPane storeSearchScrollPane = new JScrollPane(storeSearchTable);
		storeInfoArea = new JTextArea();
		storeInfoArea.setEditable(false);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(gl.createSequentialGroup()
								.addComponent(searchCityLabel)
								.addComponent(searchCityText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(searchStateLabel)
								.addComponent(searchStateText)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(searchZipLabel)
								.addComponent(searchZipText)
								)
						.addComponent(storeSearchButton)
						)
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(storeInfoArea)
						.addComponent(storeSearchScrollPane)
						)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(gl.createSequentialGroup()
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(searchCityLabel)
										.addComponent(searchCityText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(searchStateLabel)
										.addComponent(searchStateText)
										)
								.addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(searchZipLabel)
										.addComponent(searchZipText)
										)
								.addComponent(storeSearchButton)
								)
						.addGroup(gl.createSequentialGroup()
								.addComponent(storeInfoArea)
								.addComponent(storeSearchScrollPane)
								)
						)
				);
		topLevelScreen.add(storeSearchPanel, Screen.STORE_SEARCH.getName());
		
		switchScreen(Screen.ITEM_SEARCH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(itemSearchButton)) {
			lastTouchedCategory = null;
			itemSearchEvent();
		} else if(src.equals(storeSearchButton)) {
			Store s = new Store(null, null, null, searchCityText.getText(), searchStateText.getText(), searchZipText.getText(),
					null, null, null, null, null, null, null);
			Connection conn = DatabaseUtils.openConnection();
			storeList = s.find(conn);
			updateTable(storeSearchTable, storeList, STORE_SEARCH_COLS);
			DatabaseUtils.closeConnection(conn);
		} else if(src.equals(itemLookupButton)) {
			switchScreen(Screen.ITEM_SEARCH);
		} else if(src.equals(storeFinderButton)) {
			switchScreen(Screen.STORE_SEARCH);
		}
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.PAGE_AXIS));
		menuBar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO doesn't always work
				for(int i = 0; i < categoryMenu.getMenuCount(); i++) {
					categoryMenu.getMenu(i).setPopupMenuVisible(false);
				}
			}
		});
		Connection conn = DatabaseUtils.openConnection();
		List<Relation> categories = Category.getTopLevelCategories(conn);
		if(categories != null) {
			for(Relation r : categories) {
				Category cat = (Category)r;
				CategoryMenu cm = new CategoryMenu(cat);
				cm.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						CategoryMenu menu = (CategoryMenu)e.getSource();
						for(int i = 0; i < categoryMenu.getMenuCount(); i++) {
							categoryMenu.getMenu(i).setPopupMenuVisible(false);
						}
						menu.setPopupMenuVisible(true);
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						CategoryMenu menu = (CategoryMenu)e.getSource();
						menu.setPopupMenuVisible(false);
						lastTouchedCategory = menu.getCategory();
						searchBrandText.setText("");
						itemSearchEvent();
					}
				});
				List<Relation> subcategories = cat.getChildCategories(conn);
				for(Relation subr : subcategories) {
					Category subcat = (Category)subr;
					JMenuItem subCatItem = new SubcategoryMenu(subcat, cm);
					subCatItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							SubcategoryMenu menu = (SubcategoryMenu)e.getSource();
							menu.getParentMenu().setPopupMenuVisible(false);
							lastTouchedCategory = menu.getCategory();
							searchBrandText.setText("");
							itemSearchEvent();
						}
					});
					cm.add(subCatItem);
				}
				menuBar.add(cm);
			}
		}
		DatabaseUtils.closeConnection(conn);
		return menuBar;
	}
	
	private JComboBox createComboBox() {
		JComboBox combo = new JComboBox(new String[] {"None", "Price", "Popularity"});
		combo.setSelectedIndex(0);
		lastComboSelection = "None";
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox c = (JComboBox)e.getSource();
				lastComboSelection = (String)c.getSelectedItem();
				itemSearchEvent();
			}
		});
		return combo;
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
	
	private void updateTableWithPrice(JTable table, List<Relation> data, String[] columnNames) {
		String[][] tableData = Product.createTableDataWithPrice(data, columnNames);
		table.setModel(new DefaultTableModel(tableData, columnNames));
	}
	
	private void itemSearchEvent() {
		Connection conn = DatabaseUtils.openConnection();
		switch(lastComboSelection) {
		case "None":
			if(lastTouchedCategory != null) {
				productList = lastTouchedCategory.getProducts(conn, webStore);
			} else {
				Product p = new Product(null, null, searchBrandText.getText(), null);
				productList = p.find(conn, webStore);
			}
			break;
		case "Price":
			if(lastTouchedCategory != null) {
				productList = lastTouchedCategory.getProductsByPrice(conn, webStore);
			} else {
				Product p = new Product(null, null, searchBrandText.getText(), null);
				productList = p.findByPrice(conn, webStore);
			}
			break;
		case "Popularity":
			if(lastTouchedCategory != null) {
				productList = lastTouchedCategory.getProductsByPopularity(conn, webStore);
			} else {
				Product p = new Product(null, null, searchBrandText.getText(), null);
				productList = p.findByPopularity(conn, webStore);
			}
			break;
		}
		DatabaseUtils.closeConnection(conn);
		if(productList != null) {
			updateTableWithPrice(itemSearchTable, productList, ITEM_SEARCH_COLS);
		}
	}
	
	private void switchScreen(Screen screen) {
		CardLayout layout = (CardLayout)topLevelScreen.getLayout();
		layout.show(topLevelScreen, screen.getName());
		titleLabel.setText(screen.getName());
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
	
	private static String capitalize(String s) {
		StringBuilder builder = new StringBuilder();
		if(s != null) {
			boolean isFirstLetter = true;
			for(int i = 0; i < s.length(); i++) {
				char nextChar = s.charAt(i);
				if(isFirstLetter) {
					builder.append(Character.toUpperCase(nextChar));
					isFirstLetter = false;
				} else {
					builder.append(nextChar);
				}
				if(nextChar == ' ') {
					isFirstLetter = true;
				}
			}
		}
		return builder.toString();
	}

	public static void main(String[] args) {
		setLookAndFeel();
		WebApp w = new WebApp();
		w.setSize(1000, 700);
		w.setTitle("Web Interface");
		w.setDefaultCloseOperation(EXIT_ON_CLOSE);
		w.setVisible(true);
	}
	
	static class CategoryMenu extends JMenu {
        
		private Category category;
		
		CategoryMenu(Category category) {
            super(WebApp.capitalize(category.getCatName()));
            this.category = category;
            JPopupMenu pm = getPopupMenu();
            pm.setLayout(new BoxLayout(pm, BoxLayout.PAGE_AXIS));
        }
		
		@Override
		public Dimension getMinimumSize() {
            return getPreferredSize();
        }

		@Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }
		
		@Override
		public void setPopupMenuVisible(boolean b) {
            boolean isVisible = isPopupMenuVisible();
            if (b != isVisible) {
                if ((b==true) && isShowing()) {
                    int x = 0;
                    int y = 0;
                    Container parent = getParent();
                    if (parent instanceof JPopupMenu) {
                        x = 0;
                        y = getHeight();
                    } else {
                        x = getWidth();
                        y = 0;
                    }
                    getPopupMenu().show(this, x, y);
                } else {
                    getPopupMenu().setVisible(false);
                }
            }
		}
		
		public Category getCategory() {
			return category;
		}
		
    }
	
	static class SubcategoryMenu extends JMenuItem {
		
		private Category category;
		private CategoryMenu parentMenu;
		
		public SubcategoryMenu(Category category, CategoryMenu parentMenu) {
			super(WebApp.capitalize(category.getCatName()));
			this.category = category;
			this.parentMenu = parentMenu;
		}
		
		public Category getCategory() {
			return category;
		}
		
		public CategoryMenu getParentMenu() {
			return parentMenu;
		}
		
	}
	
}
