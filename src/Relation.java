import java.util.HashMap;
import java.util.List;


public abstract class Relation {
	
	protected HashMap<String, Object> attributes = new HashMap<String, Object>();
	
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
}
