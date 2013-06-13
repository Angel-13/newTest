/************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* A class that specifies the token. It has two parameters
*	- int token - a uniqe number that specifies the token, and
*	- Stromg text - a string that is the name of the token
************************************************************/
package tokens;

import java.text.MessageFormat;

import scanner.Position;


public class Token
{
	
	private final int token;
	
	private final String text;

	private final Position pos;
	
	public Token(int token, String text, Position p)
	{
		this.token = token;
		this.text = text;
		this.pos = p;
	}
    
	public Token(int token, String text)
	{
		this.token = token;
		this.text = text;
		this.pos = new Position(0,0);
	}
	
	public Token(int token, Position p)
	{
		this.token = token;
		this.text = "";
		this.pos = p;
	}
	
	public Token(int token)
	{
		this.token = token;
		this.text = "";
		this.pos = new Position(0,0);
	}
//getToken() - returns the token as a number
	public int getToken()
	{
		return this.token;
	}
//getText() - returns the token(the name of the token) as a text
	public String getText()
	{
		return this.text;
	}
//toString() - returns the token completly with the number and the text as one string
	public String toString()
	{
		Tokens t = new Tokens();
		MessageFormat messageFormat = new MessageFormat("");
		if(this.getText()==null){
			String[] args = new String[1];
			args[0] = t.tokenInt2String(this.getToken());
			return messageFormat.format("Token<{0}>", args);
		}else{
			String[] args = new String[2];
			args[0] = t.tokenInt2String(this.getToken());
			args[1] = this.getText();
			return messageFormat.format("Token<{0}, {1}>", args);
		}
	}
	
	public Position getTokenPosition(){
		return this.pos;
	}
	
}
