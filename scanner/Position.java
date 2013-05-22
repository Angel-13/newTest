/********************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* This class manages the position of the reader in the file.
* It has two parameters
* 	- raw - the postion of the raw of the the reader in the file and
*   - column - the position of column of the the reader in the file
********************************************************************/
package scanner;

import java.text.MessageFormat;

public class Position
{
	
	public int raw;
	
	public int column;

	
	public Position(int raw, int column)
	{
		this.raw = raw;
		this.column = column;
		return;
	}

	/*public int getRaw(){
		if(this.column == 0){
			return this.raw - 1;
		}
		return this.raw;
	}*/
//toString() - returns the position of the reader in the file as a string
	@SuppressWarnings("static-access")
	public String toString()
	{
		MessageFormat format = new MessageFormat("");
		Object[] arguments = new Integer[2];
		arguments[0] = new Integer(this.raw + 1);
		arguments[1] = new Integer(this.column + 1);
		return format.format("Raw {0}, Column {1}", arguments);
	}
}