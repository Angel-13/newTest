/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* The LookForwardScanner class manages the tokens.
* It purpose is to give the tokens to the parser on request one after another.
* Also it manages the position of the scanner.
************************************************************/
package scanner;

import tokens.*;

import java.io.IOException;

public class LookForwardScanner
{
	private final Scanner scanner;
	private Token currentToken;
	private Token nextToken;
	
	public LookForwardScanner(Scanner scanner) throws IOException
	{
		this.scanner = scanner;
		this.readNextToken();
	}
//getCurrentToken() - returns the current token
	public Token getCurrentToken()
	{
		return this.currentToken;
	}
//lookAhead() - returns the next token without moving the position of the scanner(reader) to the next one
	public Token lookAhead()
	{
		return this.nextToken;
	}
//readNextToken() - returns the next token and move the position of the scanner(reader) to the next one
	public Token readNextToken()
	{
		this.currentToken = this.nextToken;
		Scanner scanner = this.scanner;
		this.nextToken = scanner.getNextToken();
		return this.currentToken; 
	}
//getScanner() - returns the scanner
	public Scanner getScanner(){
		return this.scanner;
	}
//closeReader() - closes the connection between the reader and the file
	public void closeReader(){
		this.scanner.getReader().closeLookForwardReader();
	}
}