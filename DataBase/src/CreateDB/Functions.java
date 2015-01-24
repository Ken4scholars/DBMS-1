package CreateDB;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import DBMS.*;

public class Functions {
	
	private String dBpath; 
	private String tableName;
	private ArrayList<String[]> table;
	private Table tableData;
	private XMLOperations xmlOp = new XMLOperations();
	static public String databaseName;
	
	private String cleanValue(String value){
		if(value.equals("''"))
			return "null";
		if(value.charAt(0)=='\'' || value.charAt(0)=='\"')
			return value.substring(1, value.length()-1);
		return value;
	}

	public boolean createDatabase(String DatabaseName){
		File theDir = new File(DatabaseName);
		   if (!theDir.exists()) {// Check if the directory exist, if it doesn't exist then create.

		     try{
		    	 this.databaseName = DatabaseName;
		         theDir.mkdir();
		         dBpath = theDir.getAbsolutePath();

		      } catch(SecurityException e){
		    	  
		      }
		     creatSchema(DatabaseName);
		     return true;

		   }
		   else {
		    	return false;  

		   }
	}
	
	private void createTableH( String DBName, String tableName, ArrayList<String> getColumns, ArrayList<String> type){
		
		this.tableData = new Table();
		
		this.tableData.setName(tableName);
		
		Column col;
		
		int i=-1;
		for(String colName : getColumns){
			i++;
			col = new Column();
			col.setName(colName);
			col.setType(type.get(i));
			tableData.getColumns().add(col);
		}
		
		xmlOp.createTable(tableData, DBName, tableName);
		tableToSchema(DBName,tableData);
		
	}
	
	public int createTable(String dbName, String tableName, ArrayList<String> getColumns, ArrayList<String> type){
		File tableFile = new File(tableName);
		createTableH(dbName, tableName, getColumns, type);
		return 2;
	}
	
	public boolean insertIntoTable(String DBName, String tableName, ArrayList<String> values){
	
		try {
			tableData = xmlOp.readTable(DBName, tableName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		int i = 0;
		
		for(Column col : tableData.getColumns()){
			if(i < values.size()){
				col.getCells().add(cleanValue(values.get(i)));
				i++;
			}
		}
		
		xmlOp.createTable(tableData, DBName, tableName);
		return true;
		
	}
	
	
	public boolean insertIntoTable(String DBName, String tableName, ArrayList<String> columns, ArrayList<String> values){	
		try {
			tableData = xmlOp.readTable(DBName, tableName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String[] newValues = new String[tableData.getColumns().size()];

		int i = 0;
		
		for(Column col : tableData.getColumns()){
						
			for(String colName : columns){
				if(colName.equalsIgnoreCase(col.getName())){
					newValues[i] = values.get(columns.indexOf(colName));
					break;
				}
				else{
					newValues[i] = "null";
				}
			}
			i++;
		}
		
		i = 0;
		
		for(Column col : tableData.getColumns()){
			if(i < newValues.length){
				col.getCells().add(cleanValue(newValues[i]));
				i++;
			}
		}
		
		xmlOp.createTable(tableData, DBName, tableName);
		return true;
	}
	

	private  void creatSchema(String dbName){
		String path ="./"+dbName+"/schema.xml";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();
			
			Element t = doc.createElement("schema");
			doc.appendChild(t);
	
			TransformerFactory tf =  TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			
			trans.transform(source, result);
			
			
		} catch (Exception e) {
			System.out.println("Error while creating schema file");
		}
		
		
	}
	
	private void tableToSchema(String DBName  , Table t){
			
			String path = "./"+DBName+"/schema.xml";
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				Document doc = db.parse(new File(path));
				
				Node root = doc.getFirstChild();
				
				Element newTable = doc.createElement("table");
				newTable.setAttribute("name", t.getName());
				
				for (Column c : t.getColumns()) {
					
					Element col = doc.createElement("column");
					col.setAttribute("name", c.getName());
					col.setAttribute("type",c.getType() );
					
					newTable.appendChild(col);
					
				}
				root.appendChild(newTable);
				
	
				TransformerFactory tf =  TransformerFactory.newInstance();
				Transformer trans = tf.newTransformer();
				trans.setOutputProperty(OutputKeys.INDENT, "yes");
				
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(path));
				
				trans.transform(source, result);
				
				
			} catch (Exception e) {
				System.out.println("Error while creating schema file");
			}
			
			
		}
}