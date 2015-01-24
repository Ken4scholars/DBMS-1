package Parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	//Operation numbers
	// 0 for create database	// 1 for create database	//done
	// 1 for delete 			// 5 for delete				//done
	// 2 for update				// 6 for update				//done
	// 3 for create 			// 2 for create table		//done
	// 4 for select				// 4 for select 			//done
	// 5 for insert 			// 3 for insert				//done
	
	private String whereScentence = "" ;
	private String setScentence = "" ;
	private String command ;
	private String part ;
	private String[] parts ;
	private Entry data ;
	private String[] blockData ;
	private String whereExp = "((([A-Za-z0-9]|$|_){1,})\\s{0,}(=|>|<)\\s{0,}(('((\\s{0,}\\w{1,}\\s{0,}){1,})'|\"((\\s{0,}\\w{1,}\\s{0,}){1,})\")|(\\s{0,}\\d{1,}\\s{0,}))){1}";
	private String setOneExp = "(([A-Za-z0-9]|$|_){1,})(\\s{0,}=\\s{0,})(('((\\s{0,}\\w{1,}\\s{0,}){1,})'|\"((\\s{0,}\\w{1,}\\s{0,}){1,})\")|(\\s{0,}\\d{1,}\\s{0,}))";
	private String setManyExp = "(" + setOneExp + ")" + "(," + setOneExp + "){0,}";
	private Pattern setPatt = Pattern.compile(setManyExp);
	private Pattern wherePatt = Pattern.compile(whereExp);
	
	public Parser( String arg ){
		data = new Entry();
		set(arg);
		mainParser();
	}

	
	private void set(String arg) {
		arg = arg.replace(";", "  ;");
		command = arg ;
		
		part = arg ;
		part = part.trim();
		
		while( part.contains(", ") ){
			part = part.replace(", " , ",");
		}
		while( part.contains(" ,") ){
			part = part.replace(" ,", ",");
		}
		parts = part.split("\\s{1,}");
	}
	
	public void mainParser(){
		if( parts[0].equalsIgnoreCase("create") ){
			createParser();
		}
		else if( parts[0].equalsIgnoreCase("delete") ){
			deleteParser();
		}
		else if( parts[0].equalsIgnoreCase("update") ){
			updateParser();
		}
		else if( parts[0].equalsIgnoreCase("select") ){
			selectParser();
		}
		else if( parts[0].equalsIgnoreCase("insert") ){
			insertParser();
		}
		else if( parts[0].equals(";") ){
			//maybe we should handle this before input gets to parser
		}
		else{
			error();
		}
	}
	
	private void selectParserNoWhere(){
		if( isGoodBlock( parts[1] ) ){
			if( !parts[2].equalsIgnoreCase("from") || !isValidName( parts[3] ) ){
				error();
			}
			else{
				data.setOperation(4);
				data.setSelectedCols(blockData);
				data.setTableName(parts[3]);
			}
		}
		else if( parts[1].equals("*") ){
			if( !parts[2].equalsIgnoreCase("from") || !isValidName( parts[3] ) ){
				error();
			}
			else{
				data.setOperation(4);
				data.setAllColsSelected(true);
				data.setTableName(parts[3]);
			}
		}
		else{
			error();
		}
	}
	
	private void selectParserWhere(){
		for(int i=5 ; i<parts.length-1 ; ++i){
			whereScentence+=parts[i];
		}
		if( isValidWhere( whereScentence ) ){
			Matcher mtch = wherePatt.matcher(command);
			String cond = "";
			if( mtch.find() ){
				cond = mtch.group();
			}
			if( isGoodBlock( parts[1] ) ){
				if( !parts[2].equalsIgnoreCase("from") || !isValidName( parts[3] ) ){
					error();
				}
				else{
					data.setOperation(4);
					data.setSelectedCols(blockData);
					data.setTableName(parts[3]);
					data.setWhere(true);
					data.setCondition(cond);
				}
			}
			else if( parts[1].equals("*") ){
				if( !parts[2].equalsIgnoreCase("from") || !isValidName( parts[3] ) ){
					error();
				}
				else{
					data.setOperation(4);
					data.setAllColsSelected(true);
					data.setTableName(parts[3]);
					data.setWhere(true);
					data.setCondition(cond);
				}
			}
			else{
				error();
			}
		}
		else{
			error();
		}
	}
	
	private void selectParser() {
		if( parts.length == 5 ){
			selectParserNoWhere();
		}
		else if( parts.length > 5 && parts[4].equalsIgnoreCase("where")){
			selectParserWhere();
		}
		else{
			error();
		}

	}

	private boolean isGoodBlock(String block ) {
		if( !isValidBlock( block  ) ){
			return false ;
		}
		blockData = block.split(",");
		for(int i=0 ; i<blockData.length ; ++i){
			if( !isValidName( blockData[i] ) ){
				return false ;
			}
		}
		return true;
	}

	private void updateParser() {
		Pattern temp = Pattern.compile("(?i)where(?-i)");
		Matcher tmpMtch = temp.matcher(command);
		
		if( tmpMtch.find() ){
			updateWhereParser();
		}
		else{
			updateNoWhereParser();
		}
		
	}

	private void updateWhereParser() {
		if( !isValidName(parts[1]) || !parts[2].equalsIgnoreCase("set") ){
			error();
		}
		else{
			int k = 0 ;
			for(int i=3 ; !parts[i].equalsIgnoreCase("where") ; ++i){
				setScentence += parts[i];
				k = i ;
			}
			if( !isValidSet( setScentence ) ){
				error();
				return ;
			}
			for(int i=k+2 ; i<parts.length-1 ; ++i){
				whereScentence+=parts[i];
			}
			if( !isValidWhere( whereScentence ) ){
				error();
				return ;
			}
			String[] temp = command.split("(?i)where(?-i)");//from 0 set , from 1 where
			Matcher mtch = wherePatt.matcher(temp[1]);
			String cond = "";
			if( mtch.find() ){
				cond = mtch.group();
			}
			
			Matcher mtchSet = setPatt.matcher(temp[0]);
			String mySetStatement = "";
			if(mtchSet.find()){
				mySetStatement = mtchSet.group();
			}
			while( mtchSet.find() ){
				mySetStatement += "," + mtchSet.group();
			}
			
			boolean errorExists = false ;
			String[] sets = mySetStatement.split(",");
			String[] left = new String[sets.length] , right = new String[sets.length];
			
			for(int i=0 ; i<sets.length ; ++i){
				String cur[] = sets[i].split("\\s{0,}=\\s{0,}");
				left[i] = cur[0];
				right[i] = cur[1];
				if( !isValidName( left[i] ) ){
					errorExists = true ;
					break ;
				}
			}
			if( errorExists ){
				error();
			}
			else{
				data.setTableName(parts[1]);
				data.setOperation(6);
				data.setUpdateLeftSide(left);
				data.setUpdateRightSide(right);
				data.setWhere(true);
				data.setCondition(cond);
			}
			
		}
		
	}

	private void updateNoWhereParser() {
		if( !isValidName(parts[1]) || !parts[2].equalsIgnoreCase("set") ){
			error();
		}
		else{
			for(int i=3 ; i<parts.length-1 ; ++i){
				setScentence += parts[i];
			}
			if( !isValidSet( setScentence ) ){
				error();
			}
			else{
				Matcher mtch = setPatt.matcher(command);
				String mySetStatement = "";
				if(mtch.find()){
					mySetStatement = mtch.group();
				}
				while( mtch.find() ){
					mySetStatement += "," + mtch.group();
				}
				
				boolean errorExists = false ;
				String[] sets = mySetStatement.split(",");
				String[] left = new String[sets.length] , right = new String[sets.length];
				
				for(int i=0 ; i<sets.length ; ++i){
					String cur[] = sets[i].split("\\s{0,}=\\s{0,}");
					left[i] = cur[0];
					right[i] = cur[1];
					if( !isValidName( left[i] ) ){
						errorExists = true ;
						break ;
					}
				}
				if( errorExists ){
					error();
				}
				else{
					data.setTableName(parts[1]);
					data.setOperation(6);
					data.setUpdateLeftSide(left);
					data.setUpdateRightSide(right);
				}
			}
		}
		
	}

	private boolean isValidSet(String arg) {
		if( !onForm( arg , setManyExp ) ){
			return false ;
		}
		return true ;
	}

	private void deleteParser() {//+1 for semi-colon
		if( parts.length == 3 +1){//delete from tableName
			if( !parts[1].equalsIgnoreCase("from") || !isValidName(parts[2]) ){
				error();
				return ;
			}
			data.setOperation(5);
			data.setTableName(parts[2]);
		}
		else if( parts.length == 4 +1){//delete * from tableName
			if( !parts[1].equals("*") || !parts[2].equalsIgnoreCase("from") || !isValidName(parts[3]) ){
				error();
				return ;
			}
			data.setOperation(5);
			data.setTableName(parts[3]);
		}
		else if( parts.length > 4 +1){//delete from tableName where ..
			if( parts[3].equalsIgnoreCase("where") ){
				if( !parts[1].equalsIgnoreCase("from") || !isValidName(parts[2]) ){
					error();
				}
				else{
					deleteWhere();
				}
			}
			else{
				error();
			}
		}
		else{
			error();
		}
	}

	private void deleteWhere() {
		for( int i=4 ; i<parts.length-1 ; ++i ){
			whereScentence += parts[i] ;
		}
		if( isValidWhere( whereScentence ) ){
			Matcher mtch = wherePatt.matcher(command);
			String cond = "";
			if( mtch.find() ){
				cond = mtch.group();
			}
			data.setOperation(5);
			data.setTableName(parts[2]);
			data.setWhere(true);
			data.setCondition(cond);
		}
		else{
			error();
		}
	}

	private boolean isValidWhere(String arg) {
		if( !onForm( arg , whereExp ) ){
			return false ;
		}
		String[] check = arg.split("(=|>|<)");
		if( !isValidName(check[0]) ){
			return false ;
		}
		return true ;
	}

	private void error() {
		data.setError(true);
		data.setOperation(-1);
	}
	private void insertParser() {
		if(onForm(command,"(?i)insert[\\s]+into(?-i)[\\s]+([A-Za-z0-9]|$|_){1,}[\\s]+(?i)values(?-i)[\\s]*\\(([\\s]*('.+'|\".+\"|[0-9]+)[\\s]*)(,[\\s]*('.+'|\".+\"|[0-9]+)[\\s]*)*\\)[\\s]*;"))
		{
			String splitter[]=command.split("[\\s]+");
			trimmer();
			if(isValidName(splitter[2]))
			{
				
				String []stockArr = (command.substring(command.lastIndexOf("(")+1,command.lastIndexOf(")"))).split(",");
				data.setValues(stockArr);
				data.setOperation(3);
				data.setAllColsSelected(true);
				data.setTableName(splitter[2]);
				
			}
			else
				error();
		}
		else if(onForm(command,"(?i)insert[\\s]+into(?-i)[\\s]+([A-Za-z0-9]|$|_){1,}[\\s]*\\((([A-Za-z0-9]|$|_){1,})[\\s]*(([\\s]*,[\\s]*([A-Za-z0-9]|$|_){1,})[\\s]*)*\\)[\\s]*(?i)values(?-i)[\\s]*\\(([\\s]*('.+'|\".+\"|[0-9]+)[\\s]*)(,[\\s]*('.+'|\".+\"|[0-9]+)[\\s]*)*\\)[\\s]*;"))
			certainInsertion();
		else
			error();
			
	}
	private void certainInsertion()
	{
		trimmer();
		String[]splitter = command.split("[\\s]+");
		if(isValidName(splitter[2]))
		{
			String splitter1[]= (splitter[3].substring(1, splitter[3].length()-1)).split(",");
			String splitter2[] = (command.substring(command.lastIndexOf("(")+1,command.lastIndexOf(")"))).split(",");

			for(int i =0;i<splitter1.length;i++)
				if(!isValidName(splitter1[i]))
				{
					error();
					return;
				}			
			data.setOperation(3);
			data.setValues(splitter2);
			data.setSelectedCols(splitter1);
			data.setTableName(splitter[2]);
		}
		else
			error();

	}
	private void trimmer()
	{
		command = command.replace(", " , ",");
		command = command.replace(" ,", ",");
		command = command.replace("( ", "(");
		command = command.replace(" )",")");
		command = command.replace(")",") ");
		command = command.replace("(", " (");
	}


	private void createParser() {
		if(parts.length <4)
		{
			error();
			return;
		}
		if( parts[1].equalsIgnoreCase("database") ){
			if( isValidName(parts[2]) && parts.length == 4 && parts[3].equals(";") ){
				data.setOperation(1);
				data.setDatabaseName(parts[2]);
			}
			else{
				error();
			}
		}
		else{
			createTableParser();
		}
	}

	private void createTableParser() {
		if(!onForm(command,"(?i)create[\\s]+table(?-i)[\\s]+([A-Za-z0-9]|$|_){1,}[\\s]*\\([\\s]*([A-Za-z0-9]|$|_){1,}[\\s]+((?i)int(?-i)|(?i)varchar[\\s]*(?-i)\\([\\s]*[1-9]+[0-9]*[\\s]*\\)[\\s]*)(,[\\s]*([A-Za-z0-9]|$|_){1,}[\\s]+((?i)int(?-i)|(?i)varchar[\\s]*(?-i)\\([\\s]*[1-9]+[0-9]*[\\s]*\\)[\\s]*))*[\\s]*\\)[\\s]*;"))
			error();
		else
		{
			String splitter[] = command.split("[\\s]+|([\\s]*(?i)create(?-i)[\\s]*)|[\\s]*((?i)table(?-i))[\\s]*|[\\s]*;[\\s]*|[\\s]*((?i)int(?-i))[\\s]*|[\\s]*(?i)varchar[\\s]*(?-i)\\([\\s]*[1-9]+[0-9]*[\\s]*\\)[\\s]*|[\\s]*,[\\s]*|[\\s]*\\([\\s]*|[\\s]*\\)[\\s]*");
			int counter=0;
			boolean flag = true;
			for(int i =0;i<splitter.length ;i++)
				if(splitter[i].length()!=0)
					counter++;
			String needed[]=new String[counter-1];
			String table=null;
			for(int i = splitter.length-1; i>=0 ;i--)
			{
				if( splitter[i].length()!=0 )
				{
					flag = flag & isValidName(splitter[i]);
					if(counter != 1)
						needed[counter-2]=splitter[i];
					else
						table = splitter[i];
					counter--;
				}
			}
			if(!flag)
				error();
			else
			{
				data.setOperation(2);
				data.setTableName(table);
				data.setSelectedCols(needed);
				String[] stockArr = compiler("(?i)int(?-i)|(?i)varchar[\\s]*(?-i)\\([\\s]*[1-9]+[0-9]*[\\s]*\\)");
				data.setType(stockArr);
			}
				
		}
	}
	private String[] compiler(String patt)
	{
		ArrayList<String> temp = new ArrayList<String>();
		Pattern namePatt = Pattern.compile(patt);
		Matcher myMatch = namePatt.matcher(command);
		while( myMatch.find() )
			temp.add( myMatch.group());
		String[] stockArr = new String[temp.size()];
		stockArr =	temp.toArray(stockArr);
		return stockArr;

	}
	
	private boolean isValidBlock( String block ){
		if( block == null ){
			return false ;
		}
		if( block.length() < 1 ){
			return false ;
		}
		if( !onForm(block,"(([A-Za-z0-9]|$|_){1,}){1}(,([A-Za-z0-9]|$|_){1,}){0,}") ){
			return false ;
		}
		return true ;
	}
	
	private boolean isValidName( String name ){
		if( name == null ){
			return false ;
		}
		if( name.length() < 1 ){
			return false ;
		}
		if( Character.isDigit(name.charAt(0)) ){
			return false ;
		}
		for(int i=0 ; i<name.length() ; ++i){
			if( !isValidNameChar( name.charAt(i) ) ){
				return false ;
			}
		}
		if( isSqlReservedWord(name) ){
			return false ;
		}
		return true ;
	}
	
	private boolean isValidNameChar( char c ){
		if( Character.isDigit(c) || Character.isAlphabetic(c) || c=='$' || c=='_' ){
			return true ;
		}
		return false ;
	}
	
	private boolean isSqlReservedWord( String arg ){
		String[] sqlWords = { "select" , "where" , "from" , "create" , "table" , "database" ,
				"insert" , "values" , "into" , "and" , "or" , "not" , "update" , "set" , "order" ,
				"by" , "order by" , "order" , "asc" , "desc" };
		
		for( int i=0 ; i<sqlWords.length ; ++i ){
			if( arg.equalsIgnoreCase( sqlWords[i] ) ){
				return true ;
			}
		}
		
		return false ;
	}
	
	private boolean onForm( String arg , String regex ){
		return arg.matches(regex);
	}
	
	public Entry getCommand(){
		return data ;
	}
	
}
