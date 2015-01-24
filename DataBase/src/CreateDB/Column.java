package CreateDB;
import java.util.ArrayList;
 
public class Column{
	private String name;
	private String type; 
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private ArrayList<String> cells;
	public Column(){
		name = new String();
		cells = new ArrayList<String>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getCells() {
		return cells;
	}
	public void setCells(ArrayList<String> cells) {
		this.cells = cells;
	}
 
}