import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class WebApp extends JFrame {
	
	
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
	private JMenuBar categoryMenu;
	
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
		categoryMenu = new JMenuBar();
		categoryMenu.setLayout(new BoxLayout(categoryMenu, BoxLayout.PAGE_AXIS));
		resetMenuBar();
		JScrollPane menuScrollPane = new JScrollPane(categoryMenu);
		gl.setHorizontalGroup(
				gl.createSequentialGroup()
				.addComponent(menuScrollPane)
				);
		gl.setVerticalGroup(
				gl.createSequentialGroup()
				.addComponent(menuScrollPane)
				);
		topLevelScreen.add(itemSearchPanel, Screen.ITEM_SEARCH.getName());
		
		switchScreen(Screen.ITEM_SEARCH);
	}
	
	private void resetMenuBar() {
		Connection conn = DatabaseUtils.openConnection();
		populateMenuBar(Category.getTopLevelCategories(conn));
		DatabaseUtils.openConnection();
	}
	
	private void populateMenuBar(List<Relation> categories) {
		if(categories != null) {
			categoryMenu.removeAll();
			for(Relation r : categories) {
				categoryMenu.add(new JMenu(capitalize(((Category)r).getCatName())));
			}
		}
	}
	
	private String capitalize(String s) {
		String out = s;
		if(s != null && s.length() > 0) {
			out = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}
		return out;
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
	
	public static void main(String[] args) {
		setLookAndFeel();
		WebApp w = new WebApp();
		w.setSize(1000, 700);
		w.setTitle("Web Interface");
		w.setDefaultCloseOperation(EXIT_ON_CLOSE);
		w.setVisible(true);
	}
}
