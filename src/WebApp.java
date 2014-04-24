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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

public class WebApp extends JFrame implements ActionListener {
	private static final String[] ITEM_SEARCH_COLS = {"pname", "brand", "package_quantity"};//, "price"};
	
	private static enum Screen {
		ITEM_SEARCH("Item Search");
		private String name;
		private Screen(String name) { this.name = name; }		
		public String getName() { return name; }
	};
	
	private Screen currentScreen;
	
	// store logo and title
	private JPanel titlePanel;
	private ImageIcon logo;
	private JLabel titleLabel;
	
	// top-level panel
	private JPanel topLevelScreen;
	
	// item search screen
	private JPanel itemSearchPanel;
	private JLabel searchBrandLabel;
	private JTextField searchBrandText;
	private JButton itemSearchButton;
	private JMenuBar categoryMenu;
	private JTable itemSearchTable;
	
	public WebApp() {
		this.setLayout(new BorderLayout());
		GroupLayout gl;
		
		// store logo and title
		titlePanel = new JPanel(new BorderLayout());
		logo = new ImageIcon("logo.jpg");
		titleLabel = new JLabel(logo);
		titlePanel.add(titleLabel, BorderLayout.WEST);
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
		categoryMenu = new JMenuBar();
		categoryMenu.setLayout(new BoxLayout(categoryMenu, BoxLayout.PAGE_AXIS));
		createMenuBar();
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
									)
								.addComponent(itemSearchScrollPane)
								)
						)
				);
		topLevelScreen.add(itemSearchPanel, Screen.ITEM_SEARCH.getName());
		
		switchScreen(Screen.ITEM_SEARCH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton src = (JButton)e.getSource();
		if(src.equals(itemSearchButton)) {
			System.out.println("Searching....");
		}
	}
	
	private void createMenuBar() {
		categoryMenu.addMouseListener(new MouseAdapter() {
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
					public void mouseExited(MouseEvent e) {
						CategoryMenu menu = (CategoryMenu)e.getSource();
//						menu.setPopupMenuVisible(false);
						// TODO don't close unless leaving menuitems too
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO search that category and close all menus
						CategoryMenu menu = (CategoryMenu)e.getSource();
						menu.setPopupMenuVisible(false);
						Connection conn = DatabaseUtils.openConnection();
						List<Relation> results = menu.getCategory().getProducts(conn);
						DatabaseUtils.closeConnection(conn);
						if(results != null) {
							System.out.println("Clicked category " + menu.getCategory());
							updateTable(itemSearchTable, results, ITEM_SEARCH_COLS);
						}
					}
				});
				List<Relation> subcategories = cat.getChildCategories(conn);
				for(Relation subr : subcategories) {
					Category subcat = (Category)subr;
					JMenuItem subCatItem = new SubcategoryMenu(subcat, cm);
					subCatItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							// TODO search that category and close all menus
							SubcategoryMenu menu = (SubcategoryMenu)e.getSource();
							menu.getParentMenu().setPopupMenuVisible(false);
							Connection conn = DatabaseUtils.openConnection();
							List<Relation> results = menu.getCategory().getProducts(conn);
							DatabaseUtils.closeConnection(conn);
							if(results != null) {
								System.out.println("Clicked subcategory " + menu.getCategory());
								updateTable(itemSearchTable, results, ITEM_SEARCH_COLS);
							}
							
						}
					});
					cm.add(subCatItem);
				}
				categoryMenu.add(cm);
			}
		}
		DatabaseUtils.closeConnection(conn);
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
	
	private void switchScreen(Screen screen) {
		CardLayout layout = (CardLayout)topLevelScreen.getLayout();
		layout.show(topLevelScreen, screen.getName());
		titleLabel.setText(screen.getName());
		currentScreen = screen;
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
                    //Set location of popupMenu (pulldown or pullright).
                    //Perhaps this should be dictated by L&F.
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
