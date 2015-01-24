package Schema;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import Parser.*;

import CreateDB.Functions;
import DBMS.dbms;

public class SchemaFunctions {

	public String allColsTypeCheck(NodeList tableCols , ArrayList<String> val){
		
			int colSize = tableCols.getLength();
			int i = -1 ;
			for (int j = 1; j < colSize; j+=2) {
				
					i++;
					String type = tableCols.item(j).getAttributes().getNamedItem("type").getNodeValue() ;
					if((type.equals("0") && isString(val.get(i)))
								||(type.equals("1") && !isString(val.get(i))))
						return dbms.COLUMN_TYPE_MISMATCH;
			}

		return "ok";
		
	}
	public boolean DBexist(Functions f){
		
		return f.databaseName != null ;
	}
	public String columnsCheck(NodeList tableCols , ArrayList<String> col , ArrayList<String> val){
		
		boolean typeError, colNotFound ;
		
		int colSize = tableCols.getLength();
		for (int i = 0; i < col.size(); i++) {
			
			
			typeError = false ;
			colNotFound = true ;
			
			for (int j = 1; j < colSize; j+=2) {
				if(tableCols.item(j).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase(col.get(i)))
				{	
					colNotFound = false ;
					
					String type = tableCols.item(j).getAttributes().getNamedItem("type").getNodeValue() ;
					if(val!=null &&((type.equals("0") && isString(val.get(i)))
								||(type.equals("1") && !isString(val.get(i)))))
						typeError =true ;
					
					break ;
				}
				
			}
			
			if(colNotFound)
				return dbms.COLUMN_NOT_FOUND;
			if(typeError)
				return dbms.COLUMN_TYPE_MISMATCH;
			
		}
		
		return "ok";
		
	}
	public NodeList tableExists(String DBName , String tableName,Entry e){
		
		String path = "./"+DBName+"/schema.xml";
		
		DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File(path));
			
			NodeList tables = doc.getElementsByTagName("table");
			int tSize = tables.getLength();
			
			for (int i = 0; i < tSize; i++) {
				
				if(tables.item(i).getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase(tableName))
				{	e.setTableName(tables.item(i).getAttributes().getNamedItem("name").getNodeValue());
					return tables.item(i).getChildNodes();
				}
			}
		}catch(Exception ex)
		{	

		}
			
			return null ;
	}
	
	private boolean isString(String s){
			if(s.charAt(0)=='"'||"'".charAt(0)==s.charAt(0))
				return true ;
			
			return false ;
			
	}
}
