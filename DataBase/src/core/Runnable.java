package core;
import java.util.*;

public class Runnable {
	Main myMain ;
	Scanner stream ;
	String myCmd ;
	
	public Runnable() throws Throwable{
		myMain = new Main();
		stream = new Scanner(System.in);
		run();
	}
	
	void run(){
		while(true){
			myCmd = stream.nextLine();
			System.out.println(myMain.input(myCmd));
		}
	}
	
	public static void main(String[] args) throws Throwable {
		new Runnable();
	}
}
