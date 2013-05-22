/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* This class is self implemented reader, 
* which pourpose is to give us:
* 	- the position of the real reader in the file, 
* 	- the next character,
*	- the current character and
* 	- to tell us, when the reader is at the end of file.
************************************************************/
package scanner;

import java.io.IOException;
import java.io.Reader;

public class LookForwardReader
{
	private final Reader reader;
	private boolean end;
	private char currentCh;
	private char nextCh;
	private final Position pos;
	
	public LookForwardReader(Reader reader) throws IOException
	{
		this.reader = reader;
		this.pos = new Position( 1, -1);
		this.readNext();
		this.end = false;
	}
//getAkChar() - returns the current character
	public char getAkChar()
	{
		return this.currentCh;
	}
//hasNext() - Checks if the file has another character
	public boolean hasNext()
	{
		return !this.end;
	}

//The method lookAhead() give us the next character, but reader stays on the same position like before
	public char lookAhead()
	{
		if (!this.hasNext())
			throw new IllegalStateException("Es gibt kein weiteres Zeichen!");
		return this.nextCh;
	}
//The method readNext() give us the next character and move the position of the reader to the next character 
	public char readNext()
	{
		this.currentCh = this.nextCh;
		Reader r = this.reader;
		char[] readCh = new char[1];
		
		int iCount;
		try
		{
			iCount = r.read(readCh);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		if (this.currentCh == '\n')
		{
			this.pos.raw++;
			this.pos.column = 0;
		
		}
		else{
			this.pos.column++;
		}
		if (iCount == -1)
			this.end = true;
		else
			this.nextCh = readCh[0];
		return this.currentCh;
	}
//getPosition() - returns the current position of the reader
	public Position getPosition()
	{
		return this.pos;
	}
	
//closeLookForwardReader() - it closes the connection between the reader with the file
	public void closeLookForwardReader(){
		try {
			this.reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
