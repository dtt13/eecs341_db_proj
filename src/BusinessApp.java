import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;


public class BusinessApp extends JFrame {
	
	public BusinessApp() {
		
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
		WebApp w = new WebApp();
		w.setSize(1000, 700);
		w.setTitle("Business Analysis Tool");
		w.setDefaultCloseOperation(EXIT_ON_CLOSE);
		w.setVisible(true);
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
