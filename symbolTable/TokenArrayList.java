package symbolTable;

import java.util.ArrayList;

import tokens.Token;

public class TokenArrayList {
	
	private ArrayList<Token> list = new ArrayList<Token>();

	public boolean add(Token e)
	{
		return this.list.add(e);
	}

	public Token get(int index)
	{
		return this.list.get(index);
	}
	
	public int size()
	{
		return this.list.size();
	}
}
