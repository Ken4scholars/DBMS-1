package Parser;

import java.util.ArrayList;

public class Entry {
	
	 int op ;
	 String tableName ;
	 boolean error ;
	 String databaseName;
	 String[] selectedCols ;
	 public boolean allColsSelected ;
	 String condition ;
	 boolean where ;
	 String[] updateSetLeft;
	 String[] updateSetRight;
	 String[] datatype;
	 String[] values ;
	
	public void setValues( String[] arg ){
		values = arg ;
	}
	
	public void setType(String[] arg)
	{
		datatype = arg;
	}
	
	public void setWhere( boolean arg ){
		where = arg ;
	}
	
	public void setCondition( String arg ){
		condition = arg.replace(" =", "=");
		condition = condition.replace("= ", "=");
		condition = condition.replace("  =", "=");
		condition = condition.replace("=  ", "=");
		condition = condition.replace("   =", "=");
		condition = condition.replace("=   ", "=");
		condition = condition.replace("    =", "=");
		condition = condition.replace("=    ", "=");
		
		condition = condition.replace(" >", ">");
		condition = condition.replace("> ", ">");
		condition = condition.replace("  >", ">");
		condition = condition.replace(">  ", ">");
		condition = condition.replace("   >", ">");
		condition = condition.replace(">   ", ">");
		condition = condition.replace("    >", ">");
		condition = condition.replace(">    ", ">");
		
		condition = condition.replace(" <", "<");
		condition = condition.replace("< ", "<");
		condition = condition.replace("  <", "<");
		condition = condition.replace("<  ", "<");
		condition = condition.replace("   <", "<");
		condition = condition.replace("<   ", "<");
		condition = condition.replace("    <", "<");
		condition = condition.replace("<    ", "<");
	}
	
	public void setAllColsSelected( boolean arg ){
		allColsSelected = arg ;
	}
	
	public void setSelectedCols( String[] arg ){
		selectedCols = arg ;
		if( getOperNum() == 3 && !allColsSelected ){
			if( selectedCols.length != values.length ){
				setOperation(-2);
			}
		}
	}
	
	public void setTableName( String arg ){
		tableName = arg ;
	}
	
	public void setDatabaseName( String arg ){
		databaseName = arg ;
	}
	
	public void setOperation( int num ){
		op = num ;
	}
	
	public void setError( boolean val ){
		error = val ;
	}

	public void setUpdateLeftSide(String[] left) {
		updateSetLeft = left ;		
	}

	public void setUpdateRightSide(String[] right) {
		updateSetRight = right ;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public ArrayList<String> getColumns() {
		
		ArrayList<String> columns = new ArrayList<String>();
		if( getOperNum() == 6 ){
			if( updateSetLeft == null ){
				return null ;
			}
			for(int i=0 ; i<updateSetLeft.length ; ++i){
				columns.add(updateSetLeft[i]);
			}
		}
		else{
			if( selectedCols == null ){
				return null ;
			}
			for(int i=0 ; i<selectedCols.length ; ++i){
				columns.add(selectedCols[i]);
			}
		}
		return columns;
	}
	
	public ArrayList<String> getValues() {
		ArrayList<String> vals = new ArrayList<String>();
		
		if( getOperNum() == 6 ){
			if( updateSetRight == null ){
				return null ;
			}
			for(int i=0 ; i<updateSetRight.length ; ++i){
				vals.add(updateSetRight[i]);
			}
		}
		else{
			if( values == null ){
				return null ;
			}
			for(int i=0 ; i<values.length ; ++i){
				vals.add(values[i]);
			}
		}
		return vals ;
	}
	
	public int getOperNum() {
		return op ;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public ArrayList<String> getColTypes() {
		ArrayList<String> colTypes = new ArrayList<String>();
		for(int i=0 ; i<datatype.length ; ++i){
			if(datatype[i].charAt(0)=='v'){
				colTypes.add("1");
			}
			else{
				colTypes.add("0");
			}
		}
		return colTypes;
	}
	
}
