package CreateDB;
import java.util.ArrayList;
 
public class Table {
	private String name;
	private ArrayList<Column> columns;
 
	public Table() {
		name = new String();
		columns = new ArrayList<Column>();
	}
 
	public String getName() {
		return name;
	}
 
	public void setName(String name) {
		this.name = name;
	}
 
	public ArrayList<Column> getColumns() {
		return columns;
	}
 
	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}
}