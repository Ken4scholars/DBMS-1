package core;

import CreateDB.*;
import Parser.*;
import Schema.*;
import DBMS.*;

public class Main implements dbms {
	
	Parser myParser ;
	Entry command ;
	Engine myEng ;
	String str ;
	
	public Main(){
		myEng = new Engine();
	}
	
	
	public String input(String input) {
		myParser = new Parser(input);
		command = myParser.getCommand();
		if( command.getOperNum() == -1 ){
			return dbms.PARSING_ERROR ;
		}
		else if( command.getOperNum() == -2 ){
			return dbms.PARSING_ERROR ;//et2aked
		}
		try {
			return myEng.switcher(command);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return "" ;
	}

}
