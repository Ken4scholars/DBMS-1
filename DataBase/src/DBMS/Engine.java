package DBMS;

import CreateDB.*;
import Schema.*;
import Parser.*;

public class Engine {
	
	Action action = new Action();
	Functions action2 = new Functions();
	SchemaEngine schemaChecker = new SchemaEngine();
	
	public String switcher(Entry e) throws Throwable {	
		
		String resp = schemaChecker.switcher(e, action2);
		if(!resp.equals("ok")){
			return resp ;
		}
		else{
			switch (e.getOperNum()) {
			case 1:							// "CREATE DATABASE dbname";
				 action2.createDatabase(e.getDatabaseName());
				 return dbms.Con_DB;
			case 2:							// "CREATE TABLE table_name(c1,c2,...)"
				int n = action2.createTable(action2.databaseName, e.getTableName(), e.getColumns(), e.getColTypes()); 
				if(n==0)
					return dbms.DB_NOT_FOUND;
				else if (n==1)
					return dbms.TABLE_ALREADY_EXISTS; /////has'nt been done yet///////
				else
					return dbms.Con_Table;
			case 3:
				if (e.allColsSelected) // "INSERT INTO table_name VALUES(v1,...)"
					action2.insertIntoTable(action2.databaseName, e.getTableName(), e.getValues());
				else					// "INSERT INTO table_name (c1,...) VALUES (v1,...)"
					action2.insertIntoTable(action2.databaseName, e.getTableName(), e.getColumns(), e.getValues());
				return dbms.Con_insert;
			case 4:							
				if (e.getColumns() == null){ 
					if(e.getCondition() == null)	// "Select * From table"
						return action.selectFromTable(action2.databaseName, e.getTableName());
					else							// "Select * From table where..."
						return action.selectFromTable(action2.databaseName, e.getTableName(), e.getCondition())!=null? action.selectFromTable(action2.databaseName, e.getTableName(), e.getCondition()):dbms.NOT_MATCH_CRITERIA;
				} else { 						
					if(e.getCondition() == null)	// "Select c1,c2 From table"
						return action.selectFromTable(action2.databaseName, e.getTableName(), e.getColumns());
					else							// "Select c1,c2 From table where..."
						return action.selectFromTable(action2.databaseName, e.getTableName(), e.getColumns(), e.getCondition())!=null? action.selectFromTable(action2.databaseName, e.getTableName(), e.getColumns(), e.getCondition()):dbms.NOT_MATCH_CRITERIA;
				}
			case 5:							
				if(e.getCondition() == null)	// "DELETE (*) FROM table_name"
					return action.deleteFromTable(action2.databaseName, e.getTableName())==true? dbms.Con_Delete:dbms.Con_Delete;//it must return true if schema approved that the file exists
				else							// "DELETE FROM table_name where..."
					return action.deleteFromTable(action2.databaseName, e.getTableName(), e.getCondition())==true? dbms.Con_Delete:dbms.NOT_MATCH_CRITERIA;
			case 6:							
				if(e.getCondition() == null)	// "UPDATE table_name SET c1=v1,c2=v2,..."
					return action.updateTable(action2.databaseName,e.getTableName(), e.getColumns(), e.getValues())==true? dbms.Con_Update:dbms.Con_Update; 
				else							// "UPDATE table_name SET c1=v1,c2=v2,... where..."
					return action.updateTable(action2.databaseName, e.getTableName(), e.getColumns(), e.getValues(), e.getCondition())==true? dbms.Con_Update:dbms.NOT_MATCH_CRITERIA;
			default:
				break;
			}
		}
		return "Error!!!!!";
	}
	
}
