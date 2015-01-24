package Schema;
import Parser.*;
import CreateDB.Functions;
import DBMS.dbms;

import java.util.ArrayList;

import org.w3c.dom.NodeList;

public class SchemaEngine {	
	
	SchemaFunctions action = new SchemaFunctions();
	
	public String switcher(Entry e,Functions f) throws Throwable {
		
		if(e.getOperNum()!=1)
		{
			if(action.DBexist(f))
			{
				NodeList tableCols = action.tableExists(f.databaseName, e.getTableName(),e);
				if(tableCols!=null)
				{	
					
					switch (e.getOperNum()) {
					
					case 2:	
						return dbms.TABLE_ALREADY_EXISTS;
					case 3:
							
									if (e.allColsSelected ){
										if(e.getValues().size()== (tableCols.getLength())/2)
										{
											String resp = action.allColsTypeCheck(tableCols, e.getValues());
											if(!resp.equals("ok"))
												return resp ;
										}
										else
											return dbms.PARSING_ERROR;
									}
									else
									{
										String resp = action.columnsCheck(tableCols, e.getColumns(), e.getValues());
										if(!resp.equals("ok"))
											return resp ;
										
									}
						break;
						
					case 4:	
						
						if (e.getColumns() == null){ 
							if(e.getCondition() != null){	
								String[] temp = e.getCondition().split("[= > <]");
								ArrayList<String> col = new ArrayList<String>();
								ArrayList<String> val = new ArrayList<String>();
								col.add(temp[0]);
								val.add(temp[1]);
								
								String resp = action.columnsCheck(tableCols, col, val);
								if(!resp.equals("ok"))
									return resp ;
							}
						} else {
							String resp = action.columnsCheck(tableCols, e.getColumns(), null);
							if(!resp.equals("ok"))
								return resp;
							
							if(e.getCondition() != null){
								String[] temp = e.getCondition().split("[= > <]");
								ArrayList<String> col = new ArrayList<String>();
								ArrayList<String> val = new ArrayList<String>();
								col.add(temp[0]);
								val.add(temp[1]);
								
								resp = action.columnsCheck(tableCols, col, val);
								if(!resp.equals("ok"))
									return resp ;		
							}
						}
						break;
					case 5:							
						if(e.getCondition() != null){
							String[] temp = e.getCondition().split("[= > <]");
							ArrayList<String> col = new ArrayList<String>();
							ArrayList<String> val = new ArrayList<String>();
							col.add(temp[0]);
							val.add(temp[1]);
							
							String resp = action.columnsCheck(tableCols, col, val);
							if(!resp.equals("ok"))
								return resp ;		
						}	
						break;
					case 6:	
						
						String resp = action.columnsCheck(tableCols, e.getColumns(), e.getValues());
						if(!resp.equals("ok"))
							return resp ;
						
						if(e.getCondition() != null)
						{
							String[] temp = e.getCondition().split("[= > <]");
							ArrayList<String> col = new ArrayList<String>();
							ArrayList<String> val = new ArrayList<String>();
							col.add(temp[0]);
							val.add(temp[1]);
							
							 resp = action.columnsCheck(tableCols, col, val);
							if(!resp.equals("ok"))
								return resp ;	
						}
				
					   break;
					}	
					
				}
				else if(e.getOperNum()==2)
					return "ok";
				else
					return dbms.TABLE_NOT_FOUND;
			}
			else
				return dbms.DB_NOT_FOUND;
			
		}
		
		return "ok";
		
	}
	



}
