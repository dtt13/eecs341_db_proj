import java.util.HashMap;
import java.util.List;


public abstract class Relation {
	
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();
	
	public int getNumberOfAttributes() {
		return attributes.size();
	}
	
	public static String[][] createTableData(List<Relation> relations, String[] columnNames){
		String[][] output = new String[relations.size()][columnNames.length];
		for(int tuple = 0; tuple < output.length; tuple++) {
			for(int columnNum = 0; columnNum < output[tuple].length; columnNum++) {
				Object result = relations.get(tuple).attributes.get(columnNames[columnNum]);
				if(result != null) {
					output[tuple][columnNum] = result.toString();
				} else {
					output[tuple][columnNum] = "";
				}
			}
		}
		return output;
	}
	
	protected static String deformatPhone(String phoneNum) {
		return phoneNum.replaceAll("-", "");
	}
	
	protected static String formatPhone(String phoneNum) {
		StringBuilder builder = new StringBuilder();
		int index = 0;
		switch(phoneNum.length()) {
		case 11:
			builder.append(phoneNum.charAt(index++));
			builder.append(" ");
		case 10:
			builder.append(phoneNum.substring(index, index + 3));
			index += 3;
			builder.append("-");
		default:
			builder.append(phoneNum.substring(index, index + 3) + "-" + phoneNum.substring(index + 3, index + 7));
		}
		return builder.toString();
	}
}
