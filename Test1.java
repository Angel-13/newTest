import java.io.*;
import parser.*;

public class Test1{
	
	public static void main(String[] args){
			try {
				Parser p = new Parser("./Test.java");
			}
			} catch (FileNotFoundException e) {
			e.printStackTrace();
			} catch (IOException e) {
			e.printStackTrace();
		}
	}
}