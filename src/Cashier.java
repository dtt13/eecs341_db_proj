import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;


public class Cashier extends JFrame implements ActionListener {
	
	private enum Screen{STORE_SIGNIN, STORE_SELECTION};
	
	// store sign-in screen
	private JPanel storeSignInPanel;
	private JTextField sidSignInText;
	private JButton sidSignInButton;
	
	// store selection screen
	private JPanel storeSelectionPanel;
	private JTable storeTable;
	
	public Cashier() {
		// store sign-in screen
		storeSignInPanel = new JPanel(new FlowLayout());
		sidSignInText = new JTextField("Enter sid", 20);
		sidSignInButton = new JButton("Find");
		sidSignInButton.addActionListener(this);
		storeSignInPanel.add(sidSignInText);
		storeSignInPanel.add(sidSignInButton);
		
		// store selection screen
		storeSelectionPanel = new JPanel(new BorderLayout());
		storeTable = createTable();
		storeSelectionPanel.add(storeTable, BorderLayout.CENTER);
		
		switchScreen(Screen.STORE_SIGNIN);
	}
	
	private void switchScreen(Screen screen) {
//		this.remove(comp);
		switch(screen) {
		case STORE_SIGNIN:
			this.add(storeSignInPanel);
			break;
		case STORE_SELECTION:
			this.add(storeSelectionPanel);
			break;
		default:
			System.err.println("Invalid screen");
			System.exit(1);
		}
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
			Store s = new Store(sidSignInText.getText(), null, null, null,
					null, null, null, null, null, null, null, null, null);
			Connection conn = DatabaseUtils.openConnection();
			List<Store> storeList = s.find(conn);
			for(Store st : storeList) {
				System.out.println(st);
			}
			DatabaseUtils.closeConnection(conn);
			switchScreen(Screen.STORE_SELECTION);
		}
	}

	public static void main(String[] args) {
		Cashier c = new Cashier();
		c.setSize(1000, 700);
		c.setTitle("Cashier Application");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.setVisible(true);
	}
}
